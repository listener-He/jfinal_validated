package cn.listenerhe.core.sql;

import cn.hutool.core.util.StrUtil;
import com.jfinal.aop.Aop;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.*;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: hehh
 * @Date: 2018/12/28 13:22
 * @Description:
 */
@Slf4j
public abstract class BaseDb<M extends Model<M>>{

    private Class<M> modelClass;
    private M dao;
    private static final String SELET = "SELECT {} ";
    private static final String FROM = " FROM {} ";
    private static final String WHERE = " WHERE ";
    private ModelExampleSql example;
    /**
     * 获取M的class
     *
     * @return M
     */
    @SuppressWarnings("unchecked")
    private Class<M> getClazz() {
        Class<? extends BaseDb> aClass = getClass();
        Type t =aClass .getGenericSuperclass();
        if(!(t instanceof ParameterizedType)){
            Class<M> superclass = (Class<M>) aClass.getSuperclass();
            t = superclass.getGenericSuperclass();
        }
        Type[]params = ((ParameterizedType) t).getActualTypeArguments();
        return (Class<M>) DbKit.getUsefulClass((Class<M>) params[0]);
    }


    /**
     *   无参构造器 实例当前calss 、table、dao、表名称
     */
    public BaseDb() {
        this.modelClass = this.getClazz();
        this.example = new ModelExampleSql(modelClass);
        try {
            dao = modelClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 通用分页查找
     */
    public Page<M> page(int pageNumber,int pageSize) {
        return dao.paginate(pageNumber,pageSize,getSelet(null),getFrom());
    }

    /**
     * 通用分页查找 ModelExampleSql
     */
    public Page<M> pageByExample(Integer pageNumber,Integer pageSize,ModelExampleSql mll) {
        existsTable(mll);
       return dao.paginate(pageNumber,pageSize,getSelet(mll),getFrom()+WHERE+mll.getSql(),mll.getValues());
    }

    /**
     * 通用分页查找 ModelExampleSql
     */
    public Page<Record> pageByExampleResultRecord(Integer pageNumber,Integer pageSize,ModelExampleSql mll) {
        existsTable(mll);
        return Db.paginate(pageNumber,pageSize,getSelet(mll),getFrom()+WHERE+mll.getSql(),mll.getValues());
    }

    /**分页查找*/
    public Page<M> pageByModel(Integer pageNumber,Integer pageSize,M model){
         if(model == null){
              return null;
         }
        return dao.paginate(pageNumber,pageSize,getSelet(null),getFrom()+WHERE+generateSql(model.toRecord()),model.toRecord().removeNullValueColumns().getColumnValues());
    }




    /**
     * 获取记录数
     *  ModelExampleSql:sql拼接工具
     * @return
     */
    public Long findListCount(ModelExampleSql mll){
        existsTable(mll);
        mll.userDefinedProperties("count(*) AS counts");
        Record first = Db.findFirst(getSelet(mll)+getFrom()+WHERE+mll.getSql(), mll.getValues());
        if(first != null){
            return first.getLong("counts");
        }
        return 0L;
    }

    /**得到service 前半部分*/
    private String getSelet(ModelExampleSql mll){
       return StrUtil.format(SELET,mll==null || StrKit.isBlank(mll.getReultColumn())?"*":mll.getReultColumn());
    }

    /**得到from*/
    private String getFrom(){
         return StrUtil.format(FROM,example.getTableName());
    }

    /**验证ModelExampleSql*/
    private void existsTable(ModelExampleSql mll){
         if(mll == null){
              throw new RuntimeException(this.getClass()+" cn.listenerhe.core.sql.ModelExampleSql not null");
         }
         if(!mll.getTableName().equals(example.getTableName())){
             throw new RuntimeException(this.getClass()+" cn.listenerhe.core.sql.ModelExampleSql parameterModel not equals statementModel.");
         }
    }



    private String generateSql(Record record){
         if(null == record){
            throw new RuntimeException(this.getClass()+" Record not null");
         }
        Record columns = record.removeNullValueColumns();
         StringBuilder sb = new StringBuilder();
        String[] columnNames = columns.getColumnNames();
        for (int i = 0; i < columnNames.length; i++) {
            if(i > 0){
                 sb.append(ModelExampleSql.AND);
            }
            sb.append(columnNames[i]).append(ModelExampleSql.EQ).append(ModelExampleSql.placeholder);
            if(i < columnNames.length-1){
                 sb.append(ModelExampleSql.D_1);
            }
        }
        return sb.toString();
    }



    /**
     * 获取记录数
     *  whereSql:条件 如：where name=1
     * @return
     */
    public Long findListCount(String whereSql) {
        if(StrKit.isBlank(whereSql))return 0L;
        List<Record> list = new ArrayList<Record>();
        try {
            list = Db.find("select count(*) AS counts FROM "+example.getTableName()+" "+whereSql);
            if(list != null && list.size()>0){
                return list.get(0).getLong("counts");
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("获取记录数发生异常 >> SQL : {}", whereSql+" 异常原因："+e);
        }
        return 0L;
    }
}
