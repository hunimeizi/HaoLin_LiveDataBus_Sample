# HaoLin_LiveDataBus_Sample -- 组件之间的通讯LiveDataBus

 ## 关于LiveDataBus
- 从LiveData谈起
    LiveData是Android Architecture Components提出的框架。LiveData是一个可以被观察的数据持有类，
    它可以感知并遵循Activity、Fragment或Service等组件的生命周期。正是由于LiveData对组件生命周期
    可感知特点，因此可以做到仅在组件处于生命周期的激活状态时才更新UI数据。

    LiveData需要一个观察者对象，一般是Observer类的具体实现。当观察者的生命周期处于STARTED或RESUMED
    状态时，LiveData会通知观察者数据变化；在观察者处于其他状态时，即使LiveData的数据变化了，也不会通知。


- LiveData的优点

  ① UI和实时数据保持一致，因为LiveData采用的是观察者模式，这样一来就可以在数据发生改变时获得通知，
     更新UI。

  ② 避免内存泄漏，观察者被绑定到组件的生命周期上，当被绑定的组件销毁（destroy）时，观察者会立刻自动
     清理自身的数据。

  ③ 不会再产生由于Activity处于stop状态而引起的崩溃，例如：当Activity处于后台状态时，是不会收到
     LiveData的任何事件的。

  ④ 不需要再解决生命周期带来的问题，LiveData可以感知被绑定的组件的生命周期，只有在活跃状态才会通知
     数据变化。

  ⑤ 实时数据刷新，当组件处于活跃状态或者从不活跃状态到活跃状态时总是能收到最新的数据。

  ⑥ 解决Configuration Change问题，在屏幕发生旋转或者被回收再次启动，立刻就能收到最新的数据。

- LiveDataBus的组成

  ① 消息
     消息可以是任何的Object，可以定义不同类型的消息，如Boolean、String。也可以定义自定义类型的消息。

  ② 消息通道
     LiveData扮演了消息通道的角色，不同的消息通道用不同的名字区分，名字是String类型的，可以通过名字
     获取到一个LiveData消息通道。

  ③ 消息总线
     消息总线通过单例实现，不同的消息通道存放在一个HashMap中。

  ④ 订阅 Observer
     订阅者通过getChannel获取消息通道，然后调用observe订阅这个通道的消息。

  ⑤ 发布 post postValue
     发布者通过getChannel获取消息通道，然后调用setValue或者postValue发布消息。


