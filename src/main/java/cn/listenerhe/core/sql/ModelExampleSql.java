package cn.listenerhe.core.sql;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Table;
import com.jfinal.plugin.activerecord.TableMapping;
import lombok.Getter;

import java.util.*;
import java.util.function.Consumer;


/**
 *
 * @ClassName: ModelExampleSql
 * @Description: Model辅助类 用户拼接sql语句
 * 						只支持单表操作 没有特殊函数，可自定义  Date只支持 Date、DateTime、Time 不支持月周日等等
 * 					其他支持类型：var varchar,TEXT,,Integer
 * @author hehh
 * @date 2018年6月5日 下午5:12:40
 *
 */
@SuppressWarnings("all")

public final class ModelExampleSql {
	/*     -------------Model  table相关-------              */
	@Getter
	private Table t;//表结构对象
	@Getter
	private String tableName;//表名

	@Getter
	private String[] primaryKey;

	private boolean exists;   //true时，如果字段不存在就抛出异常，false时，如果不存在就不使用该字段的条件

	private boolean notNull;  //true时，如果值为空，就会抛出异常，false时，如果为空就不使用该字段的条件

	@Getter
	protected Map<String, Class<?>> selectColumnsType; //表中所有字段名称 与类型

	@Getter
	protected Set<String> selectColumns;  //表中所有字段名称

	private StringBuilder sql = new StringBuilder();  //拼接sql语句

	@Getter
	private String reultColumn; //返回属性

	private boolean banJoin = false; //禁止拼接 默认为false  在执行完 排序规则后禁用

	private String orderby; //排序

	@Getter
	private String findLimit;// 指定返回多少行

	private List<Object> values  = new ArrayList<>();

	public Object[] getValues(){
		 return values.toArray();
	}


	/*          ------  sql拼接符      --------                   */

	protected  static final  String Limit = " LIMIT ";
	protected static final String EQ = " = "; //等于

	protected static final String placeholder = " ? ";

	protected static final String D_2 = "' ";

	protected static final String D = "'";

	protected static final String D_1 = " , ";

	protected static final String IS_NOT_NULL = " IS NOT NULL "; //不等于

	protected static final String IS_NULL = " IS NULL ";

	protected static final String LESS_THEN = " < "; // 小于

	protected static final String LESS_EQUAL = " <= "; // 小于等于

	protected static final String GREATER_EQUAL = " >= "; // 大于等于

	protected static final String GREATER_THEN = " > "; // 大于

	protected static final String FUZZY = " % "; // 模糊匹配 %xxx%

	protected static final String AND = " AND "; //条件&&

	protected static final String OR = " OR "; //条件 ||

	protected static final String REGEXP = " REGEXP "; //正则匹配

	protected static final String IN = " IN "; // 在范围内

	protected static final String ORDER_BY = " ORDER BY ";

	protected static final String WHERE = " WHERE ";

	protected static final String KH_LEFT = "(";

	protected static final String KH_RIGTH = ")";

	protected static final String NOT_IN = " NOT IN ";

	protected static final String LIKE = " LIKE ";

	protected static final String NOT_LIKE = " NOT LIKE ";

	protected static final String NOT_EQ = " != ";

	protected static final String DESC = " DESC ";

	protected static final String ASC = " ASC ";

	protected static final String GROUP_BY = " GROUP BY ";

	/** 验证字段是否存在 的同时验证值*/
	public void existsColumn(String column,Object value){
		if(column ==null || (!selectColumns.contains(column) && exists)){
			throw new RuntimeException(tableName+" 表中不存在 "+column+" 字段");
		}
		if(null == value && notNull){
			throw new RuntimeException(tableName+" 表中 "+column+" 字段不允许为 null");
		}
	}

	public ModelExampleSql.Criteria createCriteria(){
			 return new Criteria();
	}


  public class Criteria{


	  private void isJoin(){
		  if(banJoin){
			  throw new RuntimeException(this.getClass() +" 方法使用错误.已终止条件拼接.");
		  }
	  }
	  /**
	   *  sql拼接
	   */
	  private void joinSQL(String joinSQL){
		  isJoin();
		  if(sql.length()>0){
			  if(!joinSQL.trim().endsWith(OR)){
				  sql.append(AND);
			  }
			  sql.append(joinSQL);
		  }else{
			  sql.append(joinSQL);
		  }
	  }

	  /** 打破and */
	  public Criteria or(){
		  if(!(sql.length()>0)){
			  throw new RuntimeException(this.getClass()+" or关键字不能刚开始就用.");
		  }

		  isJoin();
		  sql.append(OR);
		  return this;
	  }

	  /**
	   * 等于
	   * @param column 字段
	   *  @param value 值
	   */
	  public Criteria andEqualTo(String column,Object value){
		  existsColumn(column,value);
		  joinSQL(column+EQ+placeholder);
		  values.add(value);
		  return this;
	  }



	  /**
	   *  不等于
	   *
	   */
	  public Criteria andNotEqualTo(String column,Object value){
		  existsColumn(column,value);
		  joinSQL(column+NOT_EQ+placeholder);
		  values.add(value);
		  return this;
	  }

	  /**
	   *  大于 >
	   */
	  public Criteria andGreaterThan(String column, Object value) {
		  existsColumn(column,value);
		  joinSQL(column+GREATER_THEN+placeholder);
		  values.add(value);
		  return this;
	  }

	  /**
	   *  大于等于 >=
	   */
	  public Criteria andGreaterThanOrEqualTo(String column, Object value) {
		  existsColumn(column,value);
		  joinSQL(column+GREATER_EQUAL+placeholder);
		  values.add(value);
		  return this;
	  }

	  /**
	   *  < 小于
	   */
	  public Criteria andLessThan(String column, Object value){
		  existsColumn(column,value);
		  joinSQL(column+LESS_THEN+placeholder);
		  values.add(value);
		  return this;
	  }

	  /**
	   *  <= 小于等于
	   */
	  public Criteria andLessThanOrEqualTo(String column, Object value){
		  existsColumn(column,value);
		  joinSQL(column+LESS_EQUAL+placeholder);
		  values.add(value);
		  return this;
	  }

	  /**对 in 的支持*/
	  private String inValuesJoin(Collection<?> values){
		  if(values == null || CollUtil.isEmpty(values)){ return null; }

		  StringBuilder sb = new StringBuilder("(");
		  values.forEach(new Consumer<Object>() {
			  @Override
			  public void accept(Object o) {
				  sb.append(placeholder).append(D_1);
			  }
		  });
		  sb.deleteCharAt(sb.length()-1);
		  sb.append(")");
		  return sb.toString();
	  }

	  /**
	   *  在指定值中
	   */
	  public Criteria andIn(String column,Collection<?> parameterValues) {
		  existsColumn(column,parameterValues);

		  joinSQL(column+IN+inValuesJoin(parameterValues));
		  if(CollUtil.isNotEmpty(parameterValues)){
			  values.addAll(parameterValues);
		  }
		  return this;
	  }

	  /**
	   *  不在指定值中
	   */
	  public Criteria andNotIn(String column,Collection<?> parameterValues){
		  existsColumn(column,parameterValues);
		  joinSQL(column+NOT_IN+inValuesJoin(parameterValues));
		  if(CollUtil.isNotEmpty(parameterValues)){
			  values.addAll(parameterValues);
		  }
		  return this;
	  }


	  /**
	   *  不等于空
	   */
	  public Criteria andIsNotNull(String column){
		  existsColumn(column,"xxx");
		  joinSQL(IS_NOT_NULL+column);
		  return this;
	  }

	  /**
	   *  等于null
	   *
	   */
	  public Criteria andIsNull(String column){
		  existsColumn(column,"xxx");
		  joinSQL(IS_NULL+column);
		  return this;
	  }

	  /**
	   *  模糊查询 等于
	   *  	 没有加%匹配符  需手动添加
	   */
	  public Criteria andLike(String column,Object value){
		  existsColumn(column,value);
		  joinSQL(column+LIKE+placeholder);
		  values.add(value);
		  return this;

	  }


	  /**
	   *  模糊查询 不等于
	   *  	 没有加%匹配符  需手动添加
	   */
	  public Criteria andNotLike(String column,Object value){
		  existsColumn(column,value);
		  joinSQL(column+NOT_LIKE+placeholder);
		  values.add(value);
		  return this;
	  }

	  /**
	   * 手写左边条件，右边用value值
	   *
	   * @param condition 例如 "length(countryname)="
	   * @param value     例如 5
	   * @return
	   */
	  public Criteria andCondition(String condition, Object value) {
		  joinSQL(condition+placeholder);
		  values.add(value);
		  return this;
	  }


	  /**
	   * 手写条件
	   *
	   * @param condition 例如 "length(countryname)<5"
	   * @return
	   */
	  public Criteria andCondition(String condition){
		  this.joinSQL(condition);
		  return this;
	  }


	  /**
	   *  排序 ASC
	   * @param column
	   * @return
	   */
	  public Criteria orderAscBy(String column){
		  boolean[] temp = {true};
		  return orderBy(temp,column);
	  }

	  /**
	   *  排序 DESC
	   * @param column
	   * @return
	   */
	  public Criteria orderDescBy(String column){
		  boolean[] temp = {false};
		  return orderBy(temp,column);
	  }


	  /**
	   *  true：DESC
	   *  	多排序时
	   * @param sorts true 是 ASC
	   * @param column 排序字段
	   */
	  public Criteria orderBy(boolean[] sorts,String... column){
		  if(null == column){
			  throw new RuntimeException(this.getClass()+" orderBy boolean[] sorts String[] column not null");
		  }
		  if(sorts.length != column.length){
			  throw new RuntimeException(this.getClass()+" orderBy boolean[] sorts String[] column length must equals");
		  }

		  StringBuilder sb = new StringBuilder(ORDER_BY);
		  for (int i = 0; i < column.length; i++) {
			  sb.append(column[i]).append(sorts[i]?ASC:DESC);
			  if(i < column.length -1){
				  sb.append(D_1);
			  }
		  }
		  joinSQL(sb.toString());
		  return this;
	  }


  }

	/**
	 *	 默认exists为true
	 * @param entityClass
	 */
	public ModelExampleSql(Class<?> entityClass) {
		this(entityClass,true,false);
	}

	/**
	 * 带exists参数的构造方法，默认notNull为false，允许为空
	 *
	 * @param entityClass
	 * @param exists      - true时，如果字段不存在就抛出异常，false时，如果不存在就不使用该字段的条件
	 */
	public ModelExampleSql(Class<?> entityClass, boolean exists) {
		this(entityClass,exists,false);
	}

	/**
	 * 带exists参数的构造方法
	 *
	 * @param clazz
	 * @param exists      - true时，如果字段不存在就抛出异常，false时，如果不存在就不使用该字段的条件
	 * @param notNull     - true时，如果值为空，就会抛出异常，false时，如果为空就不使用该字段的条件
	 */
	public ModelExampleSql(Class<?> clazz, boolean exists, boolean notNull){
		this.classToTable(clazz);
		this.exists = exists;
		this.notNull = notNull;
	}
	/**
	 *  实例化table对象 根据传入的calss对象
	 *  		得到Model表结构
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void classToTable(Class<?> clazz){
		if(clazz == null)throw new RuntimeException("Class字节码对象不得为空..");
		this.t = TableMapping.me().getTable((Class<? extends Model>) clazz);
		if(this.t == null)throw new RuntimeException("Class异常 根据model获取表结构失败..");
		this.tableName = this.t.getName();
		this.primaryKey = t.getPrimaryKey();
		this.selectColumnsType =  this.t.getColumnTypeMap();
		this.selectColumns = this.selectColumnsType.keySet();
	}


	/**
	 *  指定要查询的属性列
	 *  		如特殊返回列 必须加上别名
	 *
	 * @param properties 返回字段
	 * @return
	 */
	public ModelExampleSql selectProperties(String... properties) {
		if (properties != null && properties.length > 0) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < properties.length; i++) {
				if(!(null != properties[i] && properties[i].trim().equals("*"))){
					existsColumn(properties[i],"xx");
					sb.append(properties[i]);
					if(i <  properties.length-1){
						sb.append(D_1);
					}
				}
			}
			this.reultColumn =sb.toString();
		}
		return this;
	}

	/***
	 *  自定义返回参数
	 * @param propertie
	 * @return
	 */
	public ModelExampleSql userDefinedProperties(String propertie){
		 if(StrKit.isBlank(propertie)){
		 	 throw new RuntimeException(this.getClass()+" Method userDefinedProperties parameter java.lang.String propertie not null");
		 }
		 if(null == this.reultColumn){
		 	 reultColumn = propertie;
		 }else{
		 	 reultColumn += D_1 + propertie;
		 }
		return this;
	}

	/**
	 *   指定返回多少行数据 指定前几行数据
	 * @param size
	 */
	public ModelExampleSql findLimit(int size){
		return this.findLimit(0,size);
	}

	/**
	 *   指定返回多少行数据 从 指定位置1开始到指定位置2结束
	 * @param size
	 */
	public ModelExampleSql findLimit(int index,int size){
		this.findLimit = Limit + index+D_1+size;
		return this;
	}

	/**
	 *  获取最后生成的sql语句
	 */
	public String getSql(){
		if(StrKit.notBlank(orderby)){
			if(!(this.sql.length()>0)){
				this.sql.append(" 1=1 ");
			}
			this.sql.append(this.orderby);
		}

		if(StrKit.notBlank(this.findLimit)){
			this.sql.append(this.findLimit);
		}
		int count = StrUtil.count(this.sql, placeholder);
		if(count > 0 && this.values.size() != count){
			throw new RuntimeException(this.getClass()+" 的占位符"+placeholder+" 与values值的长度不一致.");
		}
		if(this.sql.length()>0){
			return this.sql.toString();
		}

		throw new RuntimeException(this.getClass()+"无sql拼接");
	}

}
