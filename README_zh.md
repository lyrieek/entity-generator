# Entity Generator

首先根据数据库文件生成**缓存文件**，然后开发人员参考缓存文件配置**批注文件**，最终根据批注生成class

## 配置
database file:
```properties
jdbc.driverClassName=oracle.jdbc.OracleDriver
jdbc.url=jdbc:oracle:thin:@127.0.0.1:1521:data
jdbc.username=user
jdbc.password=qwe123
```

设置输出文件夹，以便指定生成的class文件存放位置：
```groovy
entityGenerator {
	output 'generated/po'
}
```
同时，需要将输出文件夹设置入`classes`，以便项目能够使用生成的class文件，例如：
```groovy
dependencies {
    implementation files("build/generated/po")
}
```
当然如果不需要在运行时使用，只在编译后用就无需配置了。

## 排除字段
使用以下两项配置来排除字段，被排除的字段在所有表实体中都不会生成
- _default_exclude_fields:
- _default_log_exclude_fields:

## 父类
使用以下两项配置父类，生成出来的Entity会自带这些父类：
- _default_super:
- _default_log_super:

还可以为这些父类配置构造函数的参数(单个参数)
- _default_super_arg:
- _default_log_super_arg:

请注意，在生成过程中，需要在内存中生成虚假的临时同名父类（不输出）。原因如下：
1. 暂时没有找到可以加载使用者的classloader。
2. 没有合适的时机加载这些父类。

## 序列
使用下面两项配置序列
- _default_seq:
- _default_log_seq:

- 缓存文件: 参考批注文件生成
- 批注文件: 参考缓存文件进行批注

## 计划补充
缓存文件生成优化
目标：提高缓存文件生成的效率和准确性。
分析数据库文件结构，优化生成算法，减少不必要的计算和数据处理。
增加缓存文件的版本管理，当数据库文件更新时，仅更新缓存文件中发生变化的部分，避免全量重新生成。
提供缓存文件的校验功能，确保生成的缓存文件与数据库文件一致。
