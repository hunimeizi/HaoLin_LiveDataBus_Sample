# HaoLin_LiveDataBus_Sample -- 组件之间的通讯LiveDataBus

### 关于LiveDataBus

```xml

  ① LiveData是Android Architecture Components提出的框架。LiveData是一个可以被观察的数据持有类，它可以感知并遵循
     Activity、Fragment或Service等组件的生命周期。正是由于LiveData对组件生命周期可感知特点，因此可以做到仅在组件处
     于生命周期的激活状态时才更新UI数据。

  ② LiveData需要一个观察者对象，一般是Observer类的具体实现。当观察者的生命周期处于STARTED或RESUMED状态时，LiveData会通知
     观察者数据变化；在观察者处于其他状态时，即使LiveData的数据变化了，也不会通知。

```

### LiveData的优点

```xml

  ① UI和实时数据保持一致，因为LiveData采用的是观察者模式，这样一来就可以在数据发生改变时获得通知，更新UI。

  ② 避免内存泄漏，观察者被绑定到组件的生命周期上，当被绑定的组件销毁（destroy）时，观察者会立刻自动清理自身的数据。

  ③ 不会再产生由于Activity处于stop状态而引起的崩溃，例如：当Activity处于后台状态时，是不会收到LiveData的任何事件的。

  ④ 不需要再解决生命周期带来的问题，LiveData可以感知被绑定的组件的生命周期，只有在活跃状态才会通知数据变化。

  ⑤ 实时数据刷新，当组件处于活跃状态或者从不活跃状态到活跃状态时总是能收到最新的数据。

  ⑥ 解决Configuration Change问题，在屏幕发生旋转或者被回收再次启动，立刻就能收到最新的数据。
```
### 为什么要用LiveDataBus替代EventBus和RxBus

```xml

  ① LiveDataBus的实现及其简单，相对EventBus复杂的实现，LiveDataBus只需要一个类就可以实现。

  ② LiveDataBus可以减小APK包的大小，由于LiveDataBus只依赖Android官方Android Architecture Components组件的LiveData，没
     有其他依赖，本身实现只有一个类。作为比较，EventBus JAR包大小为57kb，RxBus依赖RxJava和RxAndroid，其中RxJava2包大小
     2.2MB，RxJava1包大小1.1MB，RxAndroid包大小9kb。使用LiveDataBus可以大大减小APK包的大小。

  ③ LiveDataBus依赖方支持更好，LiveDataBus只依赖Android官方Android Architecture Components组件的LiveData，相比RxBus
     依赖的RxJava和RxAndroid，依赖方支持更好。

  ④ LiveDataBus具有生命周期感知，LiveDataBus具有生命周期感知，在Android系统中使用调用者不需要调用反注册，相比
     EventBus和RxBus使用更为方便，并且没有内存泄漏风险。

```

### LiveDataBus的组成

```xml
  ① 消息
     消息可以是任何的Object，可以定义不同类型的消息，如Boolean、String。也可以定义自定义类型的消息。

  ② 消息通道
     LiveData扮演了消息通道的角色，不同的消息通道用不同的名字区分，名字是String类型的，可以通过名字获取到一个LiveData消息
     通道。

  ③ 消息总线
     消息总线通过单例实现，不同的消息通道存放在一个HashMap中。

  ④ 订阅 Observer
     订阅者通过getChannel获取消息通道，然后调用observe订阅这个通道的消息。

  ⑤ 发布 post postValue
     发布者通过getChannel获取消息通道，然后调用setValue或者postValue发布消息。
```
### LiveDataBus的组成
- 订阅注册
```xml
    LiveDataBus.get().with("MainActivity", HuaWei.class).observe(this, new Observer<HuaWei>() {
              @Override
              public void onChanged(@Nullable HuaWei huaWei) {
                  if (huaWei != null)
                      Toast.makeText(MainActivity.this, huaWei.getName(), Toast.LENGTH_SHORT).show();
              }
          });
```

### LiveDataBus的组成
- 发送消息

```xml
        HuaWei huaWei = new HuaWei("华为","P30Pro");
        LiveDataBus.get().with("MainActivity",HuaWei.class).postValue(huaWei);
```


### 直接看效果图
<img width="531" height = "281"  src="https://github.com/hunimeizi/HaoLin_LiveDataBus_Sample/blob/master/app/livedatabus.png"/>
