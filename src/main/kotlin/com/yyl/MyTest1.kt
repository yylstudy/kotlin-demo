package com.yyl

/**
 * @Description:    TODO
 * @Author:         yang.yonglian
 * @CreateDate:     2020/4/8 19:16
 * @Version:        1.0
 */
fun main(){
    test1()
    test2()
    test3()
    test4()
    test5()
    test6()
    test7()
}
//lambda中达到return到test1()方法的效果
fun test1(){
    var list = listOf("one","two","three")
    list.forEach{
        if(it=="two"){
            //因为forEach是内联函数且是lambda，所以可以使用return
            //内联中使用return表示退出当前方法
            return
        }
        println(it)
    }
    println("-----------")
}
//lambda中达到continue的效果
fun test2(){
    var list = listOf("one","two","three")
    list.forEach {
        if(it=="two"){
            //因为forEach是内联函数且是lambda，所以可以使用return
            //如果只是返回到当前lambda，表示返回当前lambda表达式的值
            return@forEach
        }
        println(it)
    }
    println("---------")
}
//lambda中达到break的效果
fun test3(){
    run loop@{
        var list = listOf("one","two","three")
        list.forEach {
            if(it=="two"){
                return@loop
            }
            println(it)
        }
    }
    println("+++++++++++")
}

//lambda中达到break的效果，并返回值
fun test4(){
    var result = run loop@{
        var list = listOf("one","two","three")
        list.forEach {
            if(it=="two"){
                //返回具体值  格式为 return@a value
                return@loop "i am break"
            }
        }
    }
    println("$result+++++++++++")
}

/**
 * 在一个类内部为另一个类声明拓展，这个也是局部内拓展某个
 * 类的方式
 */
fun test5(){
    Animal2().printAnimal1(Animal1())
    Animal2().printList()
}
class Animal1
class Animal2{
    //为Animal1做内部拓展，可以看到这样是无法被外部函数调用的
    fun Animal1.print(){
        //当前的this指的是Animal1
        println(this)
        //如果要获取Animal，则需要this@Animal2
        println(this@Animal2)
    }
    //所以需要在Animal2中再暴露一个方法，这样就可以在外部调用到Animal1的拓展函数了
    fun printAnimal1(animal1: Animal1){
        animal1.print()
    }
    //同样，在类内部为其他公有类实现拓展也是类似道理
    fun <T> List<T>.print(){
        for(t in this){
            println(t)
        }
    }
    fun printList(){
        listOf(1,2).print()
    }
}
//数据类测试
fun test6(){
    var user1 = User("yyl1")
    user1.password = "1234"
    var user2 = User("yyl1")
    user2.password = "12345"
    //数据类只会生成主构造函数的equals，所以即使两个对象password属性不一致，也是相等的
    println(user1==user2)
    //数据解构
    var (username) = user1
    println(username)
}
data class User(var name:String){
    var password:String = "123"

}
//密封类
fun test7(){
    var p1 = Person1()
    var p2 = Person1()
    println(p1==p2)
}
//定义一个密封类,密封类的构造器是private的
//密封类和枚举差不多
sealed class Animal{
    abstract fun swim()
}
class Person1:Animal(){
    override fun swim() {
        println("Person1 can swim")
    }
}
object Person2:Animal(){
    override fun swim() {
        println("Person2 can swim")
    }
}


