1.待修改<entry key="java_typemapping.java.math.BigDecimal">Long</entry>
2.模版Service.java的create和update服务器方法修改为单个参数List，然后要推送到服务器的查询方法使用new map(参数列表)进行查询，
    这样添加或修改参数的时候，只要修改两边的sql语句即可
3.