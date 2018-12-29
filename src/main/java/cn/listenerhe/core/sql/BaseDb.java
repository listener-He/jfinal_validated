package cn.listenerhe.core.sql;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.*;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * @Auther: hehh
 * @Date: 2018/12/28 13:22
 * @Description: sql拼接执行
 */
@Slf4j
public abstract class BaseDb<M extends Model<M>>{

    private Class<M> modelClass;
    private M dao;
    private static final String SELET = "SELECT {} ";
    private static final String FROM = " FROM {} ";
    private static final String WHERE = " WHERE ";
    private static final String DELETE = " DELETE"+FROM;
    private static final String UPDATE = " UPDATE {} ";
    private ModelExample example;
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
        this.example = new ModelExample(modelClass);
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
    public Page<M> pageByExample(Integer pageNumber,Integer pageSize,ModelExample mll) {
        existsTable(mll);
       return dao.paginate(pageNumber,pageSize,getSelet(mll),getFrom()+WHERE+mll.getSql(),mll.getValues());
    }

    /**
     * 通用分页查找 ModelExampleSql
     */
    public Page<Record> pageByExampleResultRecord(Integer pageNumber,Integer pageSize,ModelExample mll) {
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
     *  获取全部
     * */
    public List<M> getAll(){
        return dao.find(getSelet(null)+getFrom());
    }

    /**
     *  根据 M 实体参数获取 忽略空值
     * @param model
     * @return
     */
    public List<M> getList(M model){
        if(model == null){ return null;}
        return dao.find(getSelet(null)+getFrom()+WHERE+generateSql(model.toRecord()),model.toRecord().removeNullValueColumns().getColumnValues());
    }


    /***
     *  条件获取
     * @param example
     * @return
     */
    public List<M> getList(ModelExample example){
         existsTable(example);
      return dao.find(getSelet(example)+getFrom()+WHERE+example.getSql(),example.getValues());
    }

    /**
     *  根据ID查询
     * @param ids
     * @return
     */
    public List<M> getByInId(Object... ids){
       return dao.find(getSelet(null)+getFrom()+WHERE+example.getPrimaryKey()[0]+ModelExample.IN,ids);
    }
    /***
     *  获取一个
     * @param example
     * @return
     */
    public M getOne(ModelExample example){
         return dao.findFirst(getSelet(example)+getFrom()+WHERE+example.getSql(),example.getValues());
    }

    /***
     *  根据key查询
     * @param primaryKeys
     * @return
     */
    public M getByPrimaryKey(Object... primaryKeys){
        if(primaryKeys == null){
             return null;
        }
        if(primaryKeys.length > example.getPrimaryKey().length){
             throw new RuntimeException(this.getClass()+" method getByPrimaryKey java.lang.Object[][] primaryKeys length greater table primaryKeys length");
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < primaryKeys.length; i++) {
            sb.append(example.getPrimaryKey()[i]).append(ModelExample.EQ).append(ModelExample.placeholder);
            if(i < primaryKeys.length-1){
                 sb.append(ModelExample.D_1);
            }
        }
        return dao.findFirst(getSelet(example)+getFrom()+WHERE+sb.toString(),primaryKeys);
    }

    /***
     *  根据id查询
     * @param id
     * @return
     */
    public M getById(Object id){
         return getByPrimaryKey(id);
    }

    /**
     *  根据id删除
     * @param id
     * @return
     */
    public long delById(Object id){
        return Db.delete(StrUtil.format(DELETE,example.getTableName())+WHERE+example.getPrimaryKey()[0]+ModelExample.EQ+"?",id);
    }


    /**
     *  根据id in删除
     * @param ids
     * @return
     */
    public long delByIds(Object... ids){
        return Db.delete(StrUtil.format(DELETE,example.getTableName())
                +WHERE+example.getPrimaryKey()[0]+ModelExample.IN+example.createCriteria().inValuesJoin(Arrays.asList(ids)),ids);
    }


    /***
     *  根据mnodel eq 删除
     * @param model
     * @return
     */
    public long delByModel(M model){
        if(null == model){return 0L;}

        return Db.delete(StrUtil.format(DELETE,example.getTableName())
                +WHERE+generateSql(model.toRecord()),model.toRecord().removeNullValueColumns().getColumnValues());
    }

    /***
     *  根据条件删除
     * @param modelExample
     * @return
     */
    public long delByExample(ModelExample modelExample){
         existsTable(modelExample);
         return Db.delete(StrUtil.format(DELETE,example.getTableName())
                 +WHERE+modelExample.getSql(),modelExample.getValues());
    }

    /***
     *　　批量修改
     * @param model
     * @return
     */
    public long update(M... model){
      return Db.batchUpdate(Arrays.asList(model), model.length > 1000 ? 1000 : model.length).length;
    }

    /***
     *  根据条件更新
     * @param model
     * @param modelExample
     * @return
     */
    public long updateByExample(M model,ModelExample modelExample){
         existsTable(modelExample);
       return  Db.update(StrUtil.format(UPDATE,example.getTableName())+generateSetSql(model,false)+WHERE+modelExample.getSql(),modelExample.getValues());
    }

    /***
     *  选择更新忽略null值
     * @param model
     * @param modelExample
     * @return
     */
    public long updateSelectice(M model,ModelExample modelExample){
        existsTable(modelExample);
        return  Db.update(StrUtil.format(UPDATE,example.getTableName())+generateSetSql(model,true)+WHERE+modelExample.getSql(),modelExample.getValues());
    }

    /***
     *  选择更新忽略null值
     * @param model 实体
     * @return
     */
    public long updateSelectice(M model){
        if(null == model || null == model.toRecord().get(example.getPrimaryKey()[0])){return 0L;}
        Object value = model.toRecord().get(example.getPrimaryKey()[0]);
        return Db.update(StrUtil.format(UPDATE,example.getTableName())+generateSetSql(model,true)+WHERE+example.getPrimaryKey()[0]+ModelExample.EQ+ModelExample.placeholder,value);
    }

    /**
     *  批量新增
     * @param models
     * @return
     */
   public long saveList(M... models){
        if(ArrayUtil.isEmpty(models)){return 0L;}
      return Db.batchSave(Arrays.asList(models),100).length;
   }

    /**
     * 获取记录数
     *  ModelExampleSql:sql拼接工具
     * @return
     */
    public Long findListCount(ModelExample mll){
        existsTable(mll);
        mll.userDefinedProperties("count(*) AS counts");
        Record first = Db.findFirst(getSelet(mll)+getFrom()+WHERE+mll.getSql(), mll.getValues());
        if(first != null){
            return first.getLong("counts");
        }
        return 0L;
    }

    /**得到service 前半部分*/
    private String getSelet(ModelExample mll){
       return StrUtil.format(SELET,mll==null || StrKit.isBlank(mll.getReultColumn())?"*":mll.getReultColumn());
    }

    /**得到from*/
    private String getFrom(){
         return StrUtil.format(FROM,example.getTableName());
    }

    /**验证ModelExampleSql*/
    private void existsTable(ModelExample mll){
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
                 sb.append(ModelExample.AND);
            }
            sb.append(columnNames[i]).append(ModelExample.EQ).append(ModelExample.placeholder);
            if(i < columnNames.length-1){
                 sb.append(ModelExample.D_1);
            }
        }
        return sb.toString();
    }

    /**
     *   生成set参数
     *    key是sql value是 值
     * @param model
     * @param loseNull 是否忽略null
     * @return
     */
    private Map<String,Object[]> generateSetSql(M model, boolean loseNull){
        if(null == model){
            throw new RuntimeException(this.getClass()+" Record not null");
        }
        Record record = model.toRecord();
        if(loseNull){
            record = record.removeNullValueColumns();
        }
        String[] columnNames = record.getColumnNames();
        if(ArrayUtil.isNotEmpty(columnNames)){
              StringBuilder sb = new StringBuilder(" SET ");
             for (int i = 0; i < columnNames.length; i++) {
                 example.existsColumn(columnNames[i],"xxx");
                  sb.append(columnNames[i]).append(ModelExample.EQ).append(ModelExample.placeholder);
                  if(i < columnNames.length -1){
                       sb.append(ModelExample.D_1);
                  }
            }

            Map<String, Object[]> objectObjectHashMap = new HashMap<>();
            objectObjectHashMap.put(sb.toString(),record.getColumnValues());
            return objectObjectHashMap;
        }
        return null;
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
