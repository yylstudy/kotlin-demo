package com.yyl

import kotlin.properties.Delegates
import kotlin.reflect.KProperty

/**
 * @Description:    TODO
 * @Author:         yang.yonglian
 * @CreateDate:     2020/4/9 15:35
 * @Version:        1.0
 */
fun main(){
    test8()
    test9()
    test10()
    test11()
    test12()
    test13()
    test14()
    test15()
}
//泛型形变
fun test8(){
    dowork(object:Source<String,Any>{
        override fun next(): String {
            return "yyl"
        }
        override fun add(v: Any) {
            println("add success:$v")
        }
    })
}
fun dowork(source: Source<String,Any>){
    //因为Source泛型声明为 out T和in V，所以这里不会编译报错，这里发生了形变和逆形变
    var source2:Source<Any,String> = source
    println(source2.next())
    source2.add("3")
}
interface Source<out T,in V>{
    //形变的泛型只能作为方法的返回值，不能作为方法的参数
    fun next():T
    //逆形变的泛型只能作为方法参数使用，不能作为方法的返回值
    fun add(v:V)
}
//因为真实情况下，很多类不能只有out或者in 这时候就需要类型投影了
fun test9(){
    var array1 = arrayOf(1,2,3)
    var array2:Array<Any> = arrayOf(4,5,6)
    //可以看到Array类既有T作为参数，又有T作为返回值，copyArray方法可以使用类型投影
    //因为from只有调用到T作为返回值的方法，所以可以使用类型投影
    copyArray(array1,array2)
    array2.forEach (::println)
}
//类型投影
fun  copyArray(from:Array<out Any>,to: Array<Any>){
    for(index in from.indices){
        to[index] = from[index]
    }
}
//伴生对象，相当于java中的静态方法所在的类
fun test10(){
    var animal1 = Companions.getObject()
    println(Companions.Companion)
}
class Companions{
    companion object{
        //类似java中的静态方法
        fun getObject():Animal1{
            return Animal1()
        }
    }
}
//委托类
fun test11(){
    var baseImpl = BaseImpl(30)
    var derived = Derived(baseImpl)
    derived.print()
    derived.print2()
}
interface Base{
    fun print()
    fun print2()
}
class BaseImpl(var age:Int):Base{
    override fun print() {
        println("age is $age")
    }
    override fun print2() {
        println("print2")
    }
}
//by baseImpl表示 baseImpl将在Derived中存储，编译器将生成转发给baseImpl的所有base方法
//可以看到委托类和继承很像，委托是实现继承的一种很好的方式，这个也类似java中的代理模式
//在kotlin中委托优于继承
class Derived(private val baseImpl:Base):Base by baseImpl{
    override fun print2() {
        baseImpl.print2()
        println("derived print2")
    }
}
//委托属性测试
fun test12(){
    println(Example().p)
    Example().p = "yyl12"
}
class Example{
    //委托属性
    var p:String by Delegate()
}
class Delegate{
    //委托属性需要实现getValue方法
    operator fun getValue(e:Example,property:KProperty<*>):String{
        println(property.name)
        return "$e${property}"
    }
    //委托属性需要实现setValue方法
    operator fun setValue(e:Example,property:KProperty<*>,str:String){
        println(property.name)
        println("setValue is $str")
    }
}
//kotlin标准库中的委托
//1)延迟属性

//标准库中的委托测试
fun test13(){
    println(lazyValue)
    //可以看到 延迟属性里的实现只会被调用一次
    println(lazyValue)
    //可观察属性
    observable = "yyl1"
    observable = "yyl2"
}
//延迟属性必须使用val修饰
val lazyValue:String by lazy {
    println("computed")
    "hello"
}
//可观察属性 yyl0表示初始值
var observable:String by Delegates.observable("yyl0"){
    prop,old,new->
    println("prop:$prop,old:$old,new:$new")
}

//属性存储在map中
fun test14(){
    var obj = FieldMap(mutableMapOf("username" to "yyl1","password" to "123"))
    println(obj.username)
    println(obj.password)
}
//把属性存储在映射中
class FieldMap(map:MutableMap<String,Any>){
    var username:String by map
    var password:String by map
}
//局部委托属性
fun test15(){
    testLazyValue{
        println("lazy value is initializable")
        30
    }
}
//局部委托属性懒加载
fun testLazyValue(resolveValue:()->Int){
    val lazyValue by lazy(resolveValue)
    //可以看到这个打印在 懒加载执行之前
    println("--------")
    lazyValue
}



