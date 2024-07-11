/*
有空時花大概1小時左右學習微服務相關技術直到懶惰或學不動為止
2024/06/19 Day1
Spring Boot篇
*/


//1.IOC(控制反轉)/DI(依賴注入)
  IoC，即Inversion of Control
  DI，即Dependency Injection

//原本寫法:
class Zoo {
    Animal tiger;
    Animal zebra;
    public Zoo() {
        tiger = new Tiger(); // 在Zoo裡面new
        zebra = new Zebra(); // 在Zoo裡面new
        tiger.eat();
        zebra.eat();
    }
}
interface Animal {
    public void eat();
}
class Tiger implements Animal {
    public void eat() {
        System.out.println("老虎吃肉");
    }
}
class Zebra implements Animal {
    public void eat() {
        System.out.println("斑馬吃草");
    }
}

/*
說明:
※在Zoo裡new Tiger(), new Zebra()，這讓Zoo和Tiger,Zebra有了結合
※Spring的設計目標之一是為了解隅，利用依賴抽象而非依賴實例的方式，因此設計了依賴注入(DI)
*/

//Spring Boot的寫法:
class Zoo {
    @Resource(name="tiger")
    Animal tiger;

    @Resource(name="zebra")
    Animal zebra;

    public Zoo() {
        tiger.eat();
        zebra.eat();
    }
}
interface Animal {
    public void eat();
}
@Component("tiger")
class Tiger implements Animal {
    public void eat() {
        System.out.println("老虎吃肉");
    }
}
@Component("zebra")
class Zebra implements Animal {
    public void eat() {
        System.out.println("斑馬吃草");
    }
}

/*
說明:
※只要宣告@Resource，不需再new Tiger(), new Zebra()，剩下的交給Spring完成。
※只要在依賴實例的地方，Spring自動會new出來，並幫我們注入，稱依賴注入。並在不需要的時候，自動收回。
※因為new的控制權從我們反轉(交給)Spring了，稱為控制反轉(IoC)
※上述會常聽到：用DI來實現IoC
*/
簡單來說是一種將新建和管理的工作交給Spring容器，開發者只需要定義對象的依賴關係，而不需要關心創建與初始化過程。
這樣讓程式更加模組化，並提高了程式的可維護性。
基本創建bean:@Component
初始化bean:@PostConstruct
注入bean:@Autowired;如有多個同類的可以搭配@Qualifier

II.AOP:切面導向設計，就是將共通邏輯寫在切面，由切面統一去處理
在class上加上@Aspect+Component就可以創建切面
常搭配的註解有@Before、@After、@Around
最常使用在權限驗證、統一的exception、Log紀錄

//New~~~New~~~New~~~New~~~New~~~New~~~New~~~New~~~New~~~New~~~//
/*
一.取得請求參數
@RequestParam取得URL參數
1.未定義的參數Spring Boot會忽略
2.少傳參數會error
3.required = false可以讓參數忽略
4.defaultValue = "22"，可以預設值
5.『http://localhost:8080/test?id=123』，是把id當成一般的url請求參數來傳遞
*/  
    @RequestMapping("/test1")
    public String test1(@RequestParam(defaultValue = "22") Integer id,
                        @RequestParam(required = false) String name),
						@RequestParam String age){
        System.out.println("id :"+id);
        System.out.println("name :"+name);
		System.out.println("age :"+age);
        return "Hello test1";
    }
/*
@RequestBody取得Body參數
1.加在method，可取得RequestBody的參數
2.body多加參數,沒定義的話，一樣可以執行，但Spring Boot會忽略掉此參數
3.body少加參數一樣可以執行
*/
    @RequestMapping("/test2")
    public String test2(@RequestBody Student student){
        System.out.println("Student id: "+student.getId());
        System.out.println("Student name: "+student.getName());
        return "Hello test2";
    }
/*
@RequestHeader取的Header參數
1.name = "Content-Type"，指定參數，跟@RequestParam一樣，但比較常使用到
2.required = false可以讓參數忽略
3.API文件上很常要取Header資訊，例如token之類的
*/
    @RequestMapping("/test3")
    public String test3(@RequestHeader String info){
        System.out.println("Header info: "+info);
        return "Hello test3";
    }
/*
@PathVariable取的URL的值
1.跟@RequestParam不太一樣
2.『http://localhost:8080/test/123』，是把123放進url路徑裡面來傳遞
*/
    @RequestMapping("/test4/{id}/{name}")
    public String test4(@PathVariable Integer id,
                        @PathVariable String name){
        System.out.println("PathVariable id: "+id);
        System.out.println("PathVariable name: "+name);
        return "Hello test4";
    }
/*
二.了解如何設計RESTful API
設計的API符合REST風格，就是RESTful API，目的:簡化溝通成本，
REST只是一個風格，依情境選擇適當的作法即可

1.使用http method表示動作
  POST/Create(新增)/新增一個資源
  GET/Read(查詢)/取得一個資源
  PUT/Update(修改)/更新一個已存在的資源
  DELETE/Delete(刪除)/刪除一個資源
2.使用url路徑描述資源之間的階層關係
GET/users -> 取得所有user
GET/users/123 -> 取得user id為123的user
GET/users/123/articles -> 取得user id為123的user所寫的文章
GET/users/123/articles/456 -> 取得user id為123的user所寫的article id為456的文章
每一個/代表一個階層(子集合的概念)
3.response body返回json或是xml格式
當class加上@RestController就是符合Rest風格的Controller，所有方法的返回值都是json
*/