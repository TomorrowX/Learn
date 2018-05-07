# Learn
My learning project

用于练习或者学习总结一些平时没有使用到的东西
计划：
- 自定义View以及ViewGroup
- 自定义动画
- 数据库
- 了解AMS，四大组件生命周期源代码
- ARN的模拟与修复
- 自动化测试*
- 硬件开发
- NDK


### 数据库

#### SQLite

##### 简介([SQLite官网](http://www.sqlite.org/about.html)):
- SQLite is an in-process library(SQLite是一个进程内的库)
- SQLite is an embedded SQL database engine（是一个嵌入式的SQL数据库引擎）
- SQLite is a compact library.（可以理解为占用空间小）
- SQLite is very carefully tested prior to every release and has a reputation for being very reliable（非常稳定）

##### 特点:
- 轻量级，没有进程内数据库，不需要C/S结构，占用内存和磁盘空间都小
- 跨平台，可移植性好（支持主流电脑OS，手机OS以及一些嵌入式OS）
- 不需要安装，单一文件
- 弱类型的字段，同一列中的数据可以是不同类型
- 开源 `:)`

##### 支持的数据类型：
一般数据采用的固定的静态数据类型，而SQLite采用的是动态数据类型，会根据存入值自动判断。SQLite具有以下五种常用的数据类型：

NULL: 这个值为空值

VARCHAR(n)：长度不固定且其最大长度为 n 的字串，n不能超过 4000。

CHAR(n)：长度固定为n的字串，n不能超过 254。

INTEGER: 值被标识为整数,依据值的大小可以依次被存储为1,2,3,4,5,6,7,8.

REAL: 所有值都是浮动的数值,被存储为8字节的IEEE浮动标记序号.

TEXT: 值为文本字符串,使用数据库编码存储(TUTF-8, UTF-16BE or UTF-16-LE).

BLOB: 值是BLOB数据块，以输入的数据格式进行存储。如何输入就如何存储,不改  变格式。

DATA ：包含了 年份、月份、日期。

TIME： 包含了 小时、分钟、秒。

##### 在Android里面的相关API：

```
SQLiteDatabase的常用方法 
openOrCreateDatabase(String path,SQLiteDatabase.CursorFactory  factory)
打开或创建数据库

insert(String table,String nullColumnHack,ContentValues  values)
插入一条记录

delete(String table,String whereClause,String[]  whereArgs)
删除一条记录

query(String table,String[] columns,String selection,String[]  selectionArgs,String groupBy,String having,String  orderBy)
查询一条记录

update(String table,ContentValues values,String whereClause,String[]  whereArgs)
修改记录

execSQL(String sql)
执行一条SQL语句

close()
关闭数据库


```
上述某些内容来自[这里](https://blog.csdn.net/codeeer/article/details/30237597/)

##### 基本使用:

创建数据库

```
db=SQLiteDatabase.openOrCreateDatabase("/data/data/{pkg}/databases/xxxx.db",null);  
```

创建数据库表

步骤：

- 编写创建数据库SQL语句
- 使用SQLiteDatabase的execSQL()方法来执行SQL语句

```
private void createTable(SQLiteDatabase db){   
    //创建表SQL语句   
    String stu_table="create table usertable(_id integer primary key autoincrement,sname text,snumber text)";   
    //执行SQL语句   
    db.execSQL(stu_table);   
}

```

增删改查数据操作也参考[这里](https://blog.csdn.net/codeeer/article/details/30237597/)

##### 监控数据库数据变化

API：android.database.ContentObserver

使用：
```
//能力：可以监听整张表，也可以监听某个字段
//使用场景:在使用数据作为数据源的时候，显示数据实时刷新数据的时候可以使用这个注册监听，然后在回调里面刷界面UI
ContentResolver.registerContentObserver(@NonNull Uri uri, boolean notifyForDescendants,
            @NonNull ContentObserver observer)
            
```


##### 关于SQLite锁与事务([参考](https://blog.csdn.net/tianzhihen_wq/article/details/45191473))

事务由3个命令控制：BEGIN、COMMIT和ROLLBACK

SQLite采用粗放型的锁。当一个连接要写数据库，所有其它的连接被锁住，直到写连接结束了它的事务。SQLite有一个加锁表，来帮助不同的写数据库都能够在最后一刻再加锁，以保证最大的并发性。

SQLite有5个不同的锁状态：

- 未加锁(UNLOCKED)
- 共享 (SHARED)
- 保留(RESERVED)
- 未决(PENDING)
- 排它(EXCLUSIVE)

每个数据库连接在同一时刻只能处于其中一个状态。每种状态(未加锁状态除外)都有一种锁与之对应。
```
    最初的状态是未加锁状态，在此状态下，连接还没有存取数据库。
    当连接到了一个数据库，甚至已经用BEGIN开始了一个事务时，连接都还处于未加锁状态。
    未加锁状态的下一个状态是共享状态。为了能够从数据库中读(不写)数据，连接必须首先进入共享状态，也就是说首先要获得一个共享锁。
    多个连接可以 同时获得并保持共享锁，也就是说多个连接可以同时从同一个数据库中读数据。
    但哪怕只有一个共享锁还没有释放，也不允许任何连接写数据库。
```



#### Cursor
Cursor对象是数据库query之后返回的一个游标对象，可以遍历游标获取需要的数据。

一些额外的使用：

MergeCursor：可以将多个游标合并成一个游标

Cursor排序：一般来说Cursor返回数据的顺序就是数据在表中的顺序，如果需要在对返回的数据进行排序的时候，有两个方法

- 将Cursor里面的数据全部取出来之后在进行数组获取其他排序（最简单，暴力，但是效率差,可能影响现有逻辑）
- 重写Cursor的遍历获取数据的方法(实现复杂，但是可以做到不影响现有逻辑)，包括：

```
boolean move(int offset);
boolean moveToPosition(int position);
boolean moveToFirst();
boolean moveToLast();
boolean moveToNext();
boolean moveToPrevious();
boolean isFirst();
boolean isLast();
boolean isBeforeFirst();
boolean isAfterLast();
int getCount();
int getPosition();

```

注：一般Cursor会搭配CursorAdapter一起使用


#### 数据共享简要（ContentProvider）

在Android里面实现跨APP或者在自己APP内部共享数据的时候可以（表示是一个选择）使用ContentProvider

一般来说ContentProvider会配合SQLite一起使用实现数据共享

使用者可以使用系统标准的API(稳定，统一，分装性好)：ContentResolver去获取或修改数据

扩展点：

平时我们使用Android自带的数据服务时(联系人，媒体等)都是使用这个方式去实现的，并且通过Binder机制
去对外暴露API接口


