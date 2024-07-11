@RestController
public class MyController {
    @RequestMapping("/test")
    public ResponseEntity<String> test(){
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("Hello World");
    }
}

測試喔
