/*
有空時花大概1小時左右學習微服務相關技術直到懶惰或學不動為止
2024/06/21 Day3
Spring Boot篇
*/

/*
『常見的Http status code』

※用來表示這次的http請求的結果為何
※分成五大類:1xx:資訊; 2xx:成功; 3xx:重新導向; 4xx:前端請求錯誤; 5xx:後端請求錯誤
詳細遇到再上網查就好!!!
*/

/*
1.ResponseEntity<?>
用法:作為方法的返回類型
用途:自定義回傳的http response的細節
*/
@RestController
public class MyController {
    @RequestMapping("/test")
    public ResponseEntity<String> test(){
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("Hello World");
    }
}

/*
1-1.HttpStatus:需要指定回傳什麼狀態碼，可進入這個class看他的實作內容來選擇。例如:「HttpStatus.ACCEPTED」就是返回202
1-2.body內的類型需要跟方法的ResponseEntity<String>裡的型態相同
*/

