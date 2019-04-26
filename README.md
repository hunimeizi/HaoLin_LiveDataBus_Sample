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

  ⑤ 发布 setValue postValue
     发布者通过getChannel获取消息通道，然后调用setValue或者postValue发布消息。
```

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

- 发送消息

```xml
        HuaWei huaWei = new HuaWei("华为","P30Pro");
        LiveDataBus.get().with("MainActivity",HuaWei.class).postValue(huaWei);
```


### LiveDataBus原理图
<img width="531" height = "281"  src="https://github.com/hunimeizi/HaoLin_LiveDataBus_Sample/blob/master/app/livedatabus.png"/>

### LiveDataBus 问题出现
-
    对于LiveDataBus的第一版实现，我们发现，在使用这个LiveDataBus的过程中，订阅者会收到订阅之前发布的消息。对于一个
    消息总线来说，这是不可接受的。无论EventBus或者RxBus，订阅方都不会收到订阅之前发出的消息。对于一个消息总线，
    LiveDataBus必须要解决这个问题。


### LiveDataBus 问题原因总结
-
    对于这个问题，总结一下发生的核心原因。对于LiveData，其初始的version是-1，当我们调用了其setValue或者postValue，
    其vesion会+1；对于每一个观察者的封装ObserverWrapper，其初始version也为-1，也就是说，每一个新注册的观察者，其
    version为-1；当LiveData设置这个ObserverWrapper的时候，如果LiveData的version大于ObserverWrapper的version，
    LiveData就会强制把当前value推送给Observer

### LiveDataBus 最终实现
- LiveDataBus 实现

```xml

        public final class LiveDataBus {
        
            private final Map<String, MutableLiveData<Object>> bus;
        
            private LiveDataBus() {
                bus = new HashMap<>();
            }
        
            private static class SingletonHolder {
                private static final LiveDataBus LIVE_DATA_BUS = new LiveDataBus();
            }
        
            public static LiveDataBus get() {
                return SingletonHolder.LIVE_DATA_BUS;
            }
            public synchronized <T> MutableLiveData<T> with(String key,Class<T> type){
                if (!bus.containsKey(key)){
                    bus.put(key,new BusMutableLiveData<>());
                }
                return (MutableLiveData<T>) bus.get(key);
            }
        }

```
- LiveDataBus 反射 使observer.mLastVersion = mVersion

```xml
        public class BusMutableLiveData<T> extends MutableLiveData<T> {
            @Override
            public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<T> observer) {
                super.observe(owner, observer);
                try {
                    hook(observer);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        
            /**
             * 反射技术  使observer.mLastVersion = mVersion
             *
             * @param observer ob
             */
            private void hook(Observer<T> observer) throws Exception {
                //根据源码 如果使observer.mLastVersion = mVersion; 就不会走 回调OnChange方法了，所以就算注册
                //也不会收到消息
                //首先获取liveData的class
                Class<LiveData> classLiveData = LiveData.class;
                //通过反射获取该类里mObserver属性对象
                Field fieldObservers = classLiveData.getDeclaredField("mObservers");
                //设置属性可以被访问
                fieldObservers.setAccessible(true);
                //获取的对象是this里这个对象值，他的值是一个map集合
                Object objectObservers = fieldObservers.get(this);
                //获取map对象的类型
                Class<?> classObservers = objectObservers.getClass();
                //获取map对象中所有的get方法
                Method methodGet = classObservers.getDeclaredMethod("get", Object.class);
                //设置get方法可以被访问
                methodGet.setAccessible(true);
                //执行该get方法，传入objectObservers对象，然后传入observer作为key的值
                Object objectWrapperEntry = methodGet.invoke(objectObservers, observer);
                //定义一个空的object对象
                Object objectWrapper = null;
                //判断objectWrapperEntry是否为Map.Entry类型
                if (objectWrapperEntry instanceof Map.Entry) {
                    objectWrapper = ((Map.Entry) objectWrapperEntry).getValue();
                }
                if (objectWrapper == null) {
                    throw new NullPointerException("Wrapper can not be null!");
                }
        
                //如果不是空 就得到该object的父类
                Class<?> classObserverWrapper = objectWrapper.getClass().getSuperclass();
                //通过他的父类的class对象，获取mLastVersion字段
                Field fieldLastVersion = classObserverWrapper.getDeclaredField("mLastVersion");
                fieldLastVersion.setAccessible(true);
                Field fieldVersion = classLiveData.getDeclaredField("mVersion");
                fieldVersion.setAccessible(true);
                Object objectVersion = fieldVersion.get(this);
                //把mVersion 字段的属性值设置给mLastVersion
                fieldLastVersion.set(objectWrapper, objectVersion);
            }
        }
        
```