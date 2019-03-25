
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>音频转文本</title>
<meta name="renderer" content="webkit">
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
<link rel="stylesheet" href="/static/lib/layui/css/layui.css"  media="all">
<link rel="stylesheet" href="/static/css/bootstrap.min.css"  media="all">
</head>
<body>

<fieldset class="layui-elem-field layui-field-title" style="margin-top: 30px;">
    <legend> 证道音频处理小工具   仅支持以下 mp3|m4a|mp4|wav|3gp|mp4|amr|wma 格式音频文件</legend>
    <legend> 操作步骤：  1、上传音频  2、转文本   3、下载文本</legend>
</fieldset>

<div class="layui-upload">
    <button type="button" class="layui-btn layui-btn-normal" id="test8">选择文件</button>
    <button type="button" class="layui-btn" id="test9">开始上传</button>
</div>

<br/>


<table class="layui-hide" id="test" lay-filter="test"></table>

<script type="text/html" id="toolbarDemo">
    <div class="layui-btn-container hide" >
        <button class="layui-btn layui-btn-sm" lay-event="getCheckData">获取选中行数据</button>
        <button class="layui-btn layui-btn-sm" lay-event="getCheckLength">获取选中数目</button>
        <button class="layui-btn layui-btn-sm" lay-event="isAll">验证是否全选</button>
    </div>
</script>

<script type="text/html" id="barDemo">
    <a class="layui-btn layui-btn-xs" lay-event="toText">转文本</a>
    <a class="layui-btn layui-btn-normal layui-btn-xs" lay-event="download">下载文本</a>
</script>




    <div class="site-demo-button hide" id="message" style="margin-bottom: 0;">
        <br/>
        <div class="layui-card-body" id="socket"></div>
    </div>



<script src="/static/lib/layui/layui.js" charset="utf-8"></script>
<script src="/static/js/jquery.min.js" charset="utf-8"></script>
<script src="/static/lib/layui/layui.all.js" charset="utf-8"></script>

<script>

    var BaseUrl =  window.location.host;

    var messageWin ;
    layui.use('table', function(){
        var table = layui.table;

        table.render({
            elem: '#test'
            ,url:'/selectVoiceTextFile'
            ,toolbar: '#toolbarDemo'
            ,title: '用户数据表'
            ,cols: [[
                {type: 'checkbox', fixed: 'left'}
                ,{field:'id', title:'ID', width:80, fixed: 'left', unresize: true, sort: true}
                ,{field:'titleName', width:300, title: '文件名',
                    templet: function(res){
                        return '<em>'+ res.titleName +'</em>'
                    }
                    }
                ,{field:'fileType', width:120, title: '类型', sort: true}
                ,{field:'fileSize', width:120, title: '大小', sort: true}
                ,{field:'createDate', width:180, title: '时间', sort: true}
                ,{field:'createDate', width:130, title: '是否已转文本',
                    templet: function(res){
                        if (res.textPath != undefined && res.textPath != ''){
                            return '<em style="color: #5FB878;">'+ '已转' +'</em>';
                        } else {
                            return '<em style="color: #FF5722;">'+ '未转' +'</em>';
                        }
                    }}
                ,{fixed: 'right', title:'操作', toolbar: '#barDemo', width:250}
            ]]
            ,page: true
        });

        //头工具栏事件
        table.on('toolbar(test)', function(obj){
            var checkStatus = table.checkStatus(obj.config.id);
            switch(obj.event){
                case 'getCheckData':
                    var data = checkStatus.data;
                    layer.alert(JSON.stringify(data));
                    break;
                case 'getCheckLength':
                    var data = checkStatus.data;
                    layer.msg('选中了：'+ data.length + ' 个');
                    break;
                case 'isAll':
                    layer.msg(checkStatus.isAll ? '全选': '未全选');
                    break;
            };
        });

        //监听行工具事件
        table.on('tool(test)', function(obj){
            var data = obj.data;
            //console.log(obj)
            if(obj.event === 'toText'){

                //  有文本存在
                if (data.textPath != undefined && data.textPath != ''){
                    layer.msg("已经转过文本可直接下载");
                    return;
                }
                var type = data.fileType;
                if ("mp3"==type || "m4a"==type
                        || "mp4"==type|| "pcm"==type
                        || "wav"==type|| "3gp"==type
                        || "amr"==type|| "wma"==type) {
                    f1();
                    toText(data.id)
                }else {
                    layer.msg("音频类型有误,请检查!");
                    return;
                }



            } else if(obj.event === 'download'){

                //  有文本存在
                if (data.textPath != undefined && data.textPath != ''){
                    window.location.href='/download?id='+data.id;
                } else {
                    layer.msg("转文本之后才可下载");
                }
            }
        });

        function f1() {

            $('#message').removeClass("hide");

             messageWin =  layer.open({
                type : 1,
                shade : 0.4,
                title : '正在执行',
                shadeClose: true, //点击遮罩关闭层
                area : [ '300px', '250px' ], //显示空间
                content : $('#message'), //捕获的元素
                cancel : function(index) {
                    layer.close(messageWin);
                    $('#message').addClass("hide"); //取消继续隐藏
                }
            });

            // 改变状态

        }

      }); // lay over


    function toText(id) {
        $.ajax({
           // url : BaseUrl+'/to/text',
            url : '/to/text',
            type : "POST",
            data : {"id" : id } ,
            success : function(data) {
                if(data.success) {
                    layer.close(messageWin);
                    $('#message').addClass("hide"); //取消继续隐藏
                    // 刷新数据
                    $(".layui-laypage-btn")[0].click();

                } else {
                    layer.msg(data.data);
                }
             },
            error : function() {

            }
        });  // ajax结束

    }

    var socket;
    if(typeof(WebSocket) == "undefined") {
        console.log("您的浏览器不支持WebSocket");
    }else{
        var url='ws://'+window.location.host+'/websocket/null';
        console.log("您的浏览器支持WebSocket");
        socket = new WebSocket(url);
        //打开事件
        socket.onopen = function() {
            console.log("Socket 已打开");
            socket.send("这是来自客户端的消息" + location.href + new Date());
        };
        //获得消息事件
        socket.onmessage = function(msg) {
            console.log(msg.data);
            //发现消息进入    开始处理前端触发逻辑
            sendMessage(msg.data);

        };
        //关闭事件
        socket.onclose = function() {
            console.log("Socket已关闭");
        };
        //发生了错误事件
        socket.onerror = function() {
            alert("Socket发生了错误");
        }

    }

    function sendMessage(message) {
        $('#socket').html('转换中，请等待，请勿关闭窗口！！！ ' + '\n' + message);
        if (message === "保存文件") {
            console.log("messageWin 已关闭");
        }
    }


    layui.use('upload', function() {
        var $ = layui.jquery , upload = layui.upload;

        //选完文件后不自动上传 voice
        upload.render({
            elem: '#test8',
            // url: '/upload/file',
            url: '/upload/voice',
            auto: false,
            multiple: false,
            bindAction: '#test9',
            accept: 'file', //音频
            exts: 'mp3|m4a|mp4|wav|3gp|mp4|amr|wma',
          //  data: { fileType: "hello", fileSize: "world" },    //额外传输的参数
            done: function(res){
                if(res.code == 200){ //上传成功
                    layer.msg("上传成功");
                    // 刷新数据
                    $(".layui-laypage-btn")[0].click();
                }else {
                    layer.msg("上传失败");
                }
            },
            error: function(res){
                layer.msg("上传失败");
            }
        });

    });

</script>

</body>
</html>