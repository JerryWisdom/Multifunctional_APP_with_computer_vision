# Multifunctional APP Based on Android and Computer Vision Model including VQA、GAN and OCR
## Here is listed my work during the whole development.

### 实现的过程与步骤

1、为了快速响应用户操作，通过将后端以及预测用到的代码文件移植到稳定的实验室服务器端，可以使安装了软件的安卓用户并发且实时地发送图片和基于图片提出的问题，然后得到服务器返回的预测结果，实现问答交互效果。

2、独立完成了视觉问答系统和Flask服务端的代码，编写了图像风格转换的后台Flask端代码供客户端交互，其安卓上的VQA模块能够以聊天的形式供用户使用，并将三个模块的后台部署在实验室服务器上运行。

3、使用EventBus，通过注册发布/订阅事件总线、发送消息简化了组件之间的通信，将事件发送者和接收者分离，在活动，片段和后台线程中表现良好，避免复杂且容易出错的依赖关系和生命周期问题，使代码更简单，速度快，具有交付线程，用户优先级等高级功能。

4、判断发送给服务器的messageInfo.getContent()是否为问题字符串String，否则为图片。视觉问答项目的路由为register（图片）和question（字符串问题），繁体字模块的路由为recOCR，图像风格转换的路由为picGAN和zcyGAN。

5、分离、编写、调试繁体字识别的后台代码，完成了繁体字定位并将其转换成对应的简体字，按照原文字位置和顺序排列输出，呈现给用户。其中我设计了res.append((识别好的文字, int(定位字的中心横坐标/定位字的平均宽度), int(定位字的中心纵坐标/定位字的平均长度)))，res = sorted(res, key=operator.itemgetter(1,2)) 这样一个算法实现文字位置的多级排序，调整文字保持顺序一致，有效解决了在字体大小范围内的截取文字区域的像素浮动误差。

6、利用 OKHTTP3 框架实现了安卓客户端和Flask服务器端的数据交互，包括发送图片或字符串给实验室服务器并获得返回结果，显示在客户端界面。

7、Web服务器端采用Flask编写，安卓端采用AndroidStudio进行开发，在完成
界面显示、利用OKHTTP框架实现从手机相册选择或直接拍摄图片并提出问题上传，以及从PC的web端服务器获取预测答案的基础上展开调试工作，其中客户端采用FormBody.Builder发送问题以及MultIPartBody.Builder（数据里有文件）发送图片数据，再利用Request、OkHttpClient和response进行交互，Flask端用两个路由分别接受post形式的图片数据。

8、解决安卓版本兼容问题，例如打不开相机。Uri代表要操作的数据，Android上可用的每种资源，包括图像、视频等都可以用Uri来表示，故拍照和相册模块中使用Uri uri = data.getData(); MessageInfo messageInfo = new MessageInfo(); image_path = getRealPathFromURI(uri)转化图片网址，然后利用file = new File(image_path)生成图片文件数据，同时以当前时间戳为图片名发送给服务器，Flask端的后台通过request.files获取ImmutableMultiDict中'image'的FileStorage并下载保存图片至本地，而问题字符串直接通过request.form.get获取textview信息即可。

9、与后台的通信模块中如何从服务器接收结果数据：
OkHttpClient okHttpClient = new OkHttpClient();
Request request = new Request.Builder().url(IP).post(builder.build()).build(); 
okHttpClient.newCall(request).enqueue(callback);
onFailure(Call call, IOException e)则服务器错误无响应，onResponse (Call call, final Response response)则说明有消息返回，通过res = response.body().string()获取；runOnUiThread(new Runnable()把更新ui的代码创建在Runnable中，然后在需要更新ui时，把这个Runnable对象传给Activity.runOnUiThread(Runnable)，Runnable对像就能在ui程序中被调用。

10、进一步完善app，增加了能够选择复制和粘贴TextView形式的繁体字识别转换结果，更加方便，提高了用户体验。

### 遇到的困难与获得的主要成果

1、开始写安卓和后台VQA交互的时候，图片发送至服务器的函数嵌入在相机拍照和相册选取图片（choosePhoto和takePhoto）功能中，增加其它模块后进一步改善，将其移植到MainActivity中，使用户在使用繁体字识别模块时能够在发送图片后顺利得到结果，区别于需要提出相应问题后才得到结果的视觉问答（VQA）模块。

2、视觉问答模块中采用了多线程，利用request.remote_addr得到的已连接内网的用户的IP地址作为字符串（需提前获取wifi服务）标记各自的图片特征（通过np.save保存不同图片特征的数组加载时直接np.load即可）和问题，从而允许多个用户同时操作，从用户发送问题到预测结果返回给APP界面过程耗时两秒左右，比较迅速，并且允许用户对同一张图片提出多个问题并得到相应回答，设置send_cnt和flag字段分别对安卓发送的和后台接收的字符串加以标记，每当发送一个新的图片则重置send_cnt为0。

3、服务器后台运行状态不够稳定，而识别的字数量不同导致运行时间不一致，快则20秒之内，慢则两三分钟，采用OkHttpClient.Builder().connectTimeout(3, 
TimeUnit.SECONDS).readTimeout (200, TimeUnit.SECONDS)，这样以秒为单位设置读取超时时间为200s，就能保持与后台的连接，使客户端成功获取结果。

4、如何接收服务器返回的图片文件，并在base64编码、bitmap、uri之间相互转换：byte[] decoded = Base64.decode(res, Base64.DEFAULT);
Bitmap decodedByte = BitmapFactory.decodeByteArray(decoded, 0, decoded.length);
imageView.setImageBitmap(decodedByte) 得以在图片显示控件中显示，
Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), 
decodedByte, null,null)); message.setImageUrl(getRealPathFromURI(uri)) 根据根据图片的uri获取图片的绝对路径并加载显示。

### 个人测试与运行记录

1、手机或安卓模拟器连寝室wifi的IP地址是本地局域网，而不是服务器上识别到的学校内网的客户端地址，所以手机app进入视觉问答模块发给服务器的问题字符串中IP标记字段不一致，导致接受到的问题为None，需要注意。

2、当点击Fragment3的VQA模块按钮进入相应的activity页面时，发送图片会同时相应前两个Fragment里GAN和OCR模块里的服务器端口，十分错乱，导致原本应该响应的register和question端口没有反应或延迟，经过调试后发现是Eventbus的扫尾工作问题，在前两个模块里加上以下代码后解决了问题。
public void onHiddenChanged(boolean hidden) {
    super.onHiddenChanged(hidden);
if(hidden){ 
//不在最前端显示，相当于调用onPause()，退出Fragment执行生命周期，这样就可以单独处理新的的Fragment的数据和ui的刷新了。
        EventBus.getDefault().unregister(this);
    }else {  //在最前端显示
        EventBus.getDefault().register(this);  //重新注册事件总线
    }
}

### 结果分析与个人小结

将最后调试生成的图像识别apk文件安装在手机上运行，打开运行挂在服务器的后台文件，各个模块随意点击并使用，无任何闪退现象，生成的结果较为满意，且响应及时，除界面设计外用户体验较佳。
经过短短几天的软件开发实践，从调试图像视觉的模型算法，到编写Flask端后台，然后构建安卓客户端和后台的交互，最后调通整个客户端框架里的各个模块，同时熟悉了安卓的前端设计和用户交互，还有将各个模块进行整合，获益匪浅。在开发过程中，遇到问题就要列好各个击破，及时解决，静下心思考，缕清思路并不难，解决了就能够防止在类似的问题上绕弯路而浪费时间，按照软件开发的基本步骤进行开发调试，每个功能模块细化，用函数的形式进行封装方便调用和修改。
