# Entity Generator

首先根据数据库文件生成原始文件，然后开发人员参考原始文件配置批注文件，最终根据批注生成class

## 父类的配置
使用下面两项配置父类：
_default_sub:
_default_log_sub:

请注意,生成过程中要在内存中生成虚假的临时同名父类(不输出), 1是因为暂时没有找到可以加载使用者的classloader, 2是因为没有合适的时机加载
