package com.yyl

import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.thread

/**
 * @Description:    lambda函数
 * @Author:         yang.yonglian
 * @CreateDate:     2020/4/10 11:11
 * @Version:        1.0
 */
fun main(){
    test151()
    test16()
    test17()
    test18()
    test19()
    test20()
}
//kotlin中lambda和java中lambda的区别
fun test151(){
    //定义一个已存在的函数式接口的lambda对象，kotlin中一定要带上所定义的函数式对象的类名
    var runn = Runnable {
        println("t am run in runnable")
    }
    //定义一个自定义格式的lambda的对象
    var runnn:(Int)->Int = {
        it*it
    }
    Thread(runn).run()
}
//lambda测试
fun test16(){
    var value = listOf(3,2,1).fold2(3){
        foldValue,item->
        foldValue+item*2
    }
    println(value)
}
//给出一个起始值，收集集合中的值
fun <T,V> List<T>.fold2(initValue:V,resolveFun:(V,T)->V):V{
    var foldValue = initValue
    for(it in this){
        foldValue = resolveFun(foldValue,it)
    }
    return foldValue
}
//带接收者的函数字面值，这个就是类似于在已有类上定义一个拓展函数，只是这个拓展函数是lambda的函数
fun test17(){
    var sumFun:Int.(Int)->Int = {
        //this表示调用此lambda函数的对象
        println(this)
        //it表示传入此lambda函数的参数
        println(it)
        this+it
    }
    var number1 = 30
    //使用类似拓展函数调用
    println(number1.sumFun(2))
    //带接收者的函数类型和不带接收者的函数类型可以互换，A.(B)->C 可以替换为 (A,B)->C，反之也可以
    //所以上面也可以使用下面这种方式调用
    println(sumFun(number1,2))
}
//内联函数
fun test18(){
    lock(ReentrantLock()){
        println("i am working in lock")
    }
}
//内联函数，方法使用inline关键字修饰，内联函数的原理就是程序在编译时期会把调用到这个函数
//的地方用这个函数方法体进行替换，避免方法出栈和入栈，提升性能，一个方法被定义为内联
//那么他的参数也都是内联的
inline fun <T> lock(lock:Lock,noinline body:()->T):T{
    try{
        lock.lock()
        //如果方法是inline，那么其参数也都是内联的，也就是在编译器会被替换具体的值
        //但是方法resolveBody不是内联的，所以需要的是一个对象，这样就会报错了
        //解决方法由两个，一是resolveBody方法也定义为inline，二是 body参数定义为noline
        //所以noline总的来说就是内联函数的参数也是函数，但是被其他非内联函数调用到，
        //那么就需要将这个参数定义为noinline
        resolveBody(body)
        return body()
    }finally {
        lock.unlock()
    }
}
fun <T> resolveBody(body:()->T):T{
    return body()
}
//crossinline 测试
fun test19(){
    var value = method{
        it*it
    }.test(3)
    println(value)
}
//crossinline的作用是内联函数中让内标记为crossinline的参数的lambda的表达式不允许非局部返回
//因为内联的lambda是允许非局部返回的，也就是退出调用创建lambda的函数
//crossinline 就是表示t函数中不能使用return，没太看懂其实
inline fun method(crossinline t:(Int)->Int):TestInter{
    return object:TestInter{
        override fun test(a: Int): Int {
            return t(a)
        }
    }
}
interface TestInter{
    fun test(a:Int):Int
}
//测试内联函数的具体化类型参数
fun test20(){
    var p1 = Node1("yyl1")
    var p2 = Node1("yyl2")
    var p3 = Node2("yyl3")
    p1.parent = p2
    p2.parent = p3
    //使用反射进行类型判断
    var parent = p1.findParentByType(Node2::class.java)
    //使用内联函数具体化类型参数进行类型的判断
    var parent2 = p1.findParentByTypeByInline<Node2>()
    println(parent?.name)
    println(parent2?.name)
}

open class Node(var name:String){
    lateinit var parent:Node
}
class Node1(name:String):Node(name)
class Node2(name:String):Node(name)
//利用反射，获取对应父类型的对象
fun <T> Node.findParentByType(clazz:Class<T>):T?{
    var parents = parent
    while(parents!=null&&!clazz.isInstance(parents)){
        parents = parents.parent
    }
    return parents as T?
}
//内联函数支持具体化类型参数，使用reified关键字对泛型进行标识，这样
//在其方法内部就可以直接使用这个泛型进行判断了
inline fun <reified T> Node.findParentByTypeByInline():T?{
    var parents = parent
    while(parents!=null&&parents !is T){
        parents = parents.parent
    }
    return parents as T?
}