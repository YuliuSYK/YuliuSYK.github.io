$(function () {
  var run = 0,
    heading = $("h1"),
    timer;
  $("#start").click(function () {
    $("#stop_u").hide();
    // 读取rec/list.txt
    var list = "";
    $.ajax({
      url: "/rec/list.txt",
      dataType: "text",
      success: function (data) {
        list = data.replace(/(\r\n|\n|\r)/gm, "\r\n").split("\r\n");
        // 对list中的数据进行过滤，去掉空格，空行，注释行
        list = list.filter(function (item) {
          return item && item.trim() && !item.startsWith("#");
        });
      },
    });
    if (!run) {
      heading.html(heading.html().replace("就决定是你了！", "到底要选哪个呢？"));
      $(this).val("停止");
      timer = setInterval(function () {
        var r = Math.ceil(Math.random() * list.length),
          food = list[r - 1];
        $("#what").html(food);
        var rTop = Math.ceil(Math.random() * $(document).height()),
          rLeft = Math.ceil(Math.random() * ($(document).width() - 50)),
          rSize = Math.ceil(Math.random() * (37 - 14) + 14);
        $("<span class='temp'></span>")
          .html(food)
          .hide()
          .css({
            top: rTop,
            left: rLeft,
            color: "rgba(0,0,0,." + Math.random() + ")",
            fontSize: rSize + "px",
          })
          .appendTo("body")
          .fadeIn("slow", function () {
            $(this).fadeOut("slow", function () {
              $(this).remove();
            });
          });
      }, 50);
      run = 1;
    } else {
      $("#stop_u").show();
      heading.html(heading.html().replace("吃什么？", "吃这个！"));
      $(this).val("吃什么？");
      clearInterval(timer);
      run = 0;
    }
  });


  document.onkeydown = function enter(e) {
    var e = e || event;
    if (e.keyCode == 13) $("#start").trigger("click");
  };
});

/*
$i = 0;
$("#start").click(function () {
  $i++;
  if ($i >= 6) {
    $("#start").hide();
    $("#stop").show();
  }
});
$("#stop").click(function () {
  alert("这么作？今天别吃了！");
  $(this).hide();
});*/
