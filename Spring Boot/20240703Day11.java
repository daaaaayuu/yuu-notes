/*
有空時花大概1小時左右學習微服務相關技術直到懶惰或學不動為止
2024/07/03 Day11
Spring Boot篇
*/

/*【Spring Data JPA】*/
//※Spring JDBC
  在Spring Boot中執行"原始的SQL語法"，去操作資料庫
  『開發效率低，效能較好，可寫出複雜的SQL』
    
//※Spring Data JPA
  "使用ORM的概念"，透過操作Java Object的方式，去操作資料庫
  ORM(Object-Relational Mapping):將"Java Object，去對應到資料庫的table"，所以對Java Object的操作就是對資料庫的操作
  『開發效率高，效能較差，很難寫出複雜的查詢』
  
  JPA(Java Persistence API):
     "定義"要如何操作資料庫
     JPA提供了許多註解讓我們使用，像是@Entity、@Table、@Column......
  Hibernate:
     一種ORM框架，去"實作"JPA
     負責自動生成SQL語法

//一.連線設定
   1.pom.xml:
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>

        <!-- https://mvnrepository.com/artifact/mysql/mysql-connector-java -->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>8.0.22</version>
        </dependency>
   
   2.application.properties:
       spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
       spring.datasource.url=jdbc:mysql://localhost:3306/myjpa?serverTimezone=Asia/Taipei&characterEncoding=utf-8
       spring.datasource.username=root
       spring.datasource.password=springboot
   
//二.實作Spring Data JPA
    
//1.在資料庫建立student的table

//2.新增一個對應的class
@Entity:用來對應DB
@Table(name="student"):將這個Java class對應到student table
@Column(name = "id"):將變數對應到id這個Column上
@Id:因為id是PK所以要加上這個
@GeneratedValue(strategy = GenerationType.IDENTITY):因為id是會自動增加，所以要加上這個，代表id這個值是由資料庫生成的，使用IDENTITY的方式生成


@Entity
@Table(name = "student")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Integer id;

    @Column(name = "name")
    String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

//3.新增一個interface繼承CrudRepository
public interface StudentRepository extends CrudRepository<Student,Integer> {
}

//4.最後新增一個controller來實際使用CrudRepository
@RestController
public class StudentController {

    @Autowired
    private StudentRepository studentRepository;

    @PostMapping("/students")
    public String insert(@RequestBody Student student){

        studentRepository.save(student);

        return "執行資料庫的Create 操作";
    }
}

//三.使用CrudRepository來做CRUD
※CrudRepository:基本的CRUD操作 <---最常用
^繼承
※PaginAndSortingRepository:新增分頁和排序的操作
^繼承
※JpaRepository:能力最強，新增了JPA相關的flush操作

※save(S entity):可以新增/修改

※Optional<T> findById(ID id):根據ID的值，去資料庫查詢
  ***Optional是JAVA8才有的類型***

※void deleteById(ID id):根據ID的值，去資料庫刪除

※在properties新增spring.jpa.show-sql=true，就可以在console看到sql語法

//1.新增
    @PostMapping("/students")
    public String insert(@RequestBody Student student){

        studentRepository.save(student);

        return "執行資料庫的 Create 操作";
    }

//2.修改
因為save()，同時有新增/修改功能，如果要修改資料的話，建議先查詢DB是否有這筆資料，有的話在執行
不然沒資料會直接新增，就不是預期中的UPDATE了

    @PutMapping("/students/{studentId}")
    public String update(@PathVariable Integer studentId,
                         @RequestBody Student student){

        student.setId(studentId);
        studentRepository.save(student);

        return "執行資料庫的 Update 操作";
    }

加上查詢的作法
    @PutMapping("/students/{studentId}")
    public String update(@PathVariable Integer studentId,
                         @RequestBody Student student){

        Student s = studentRepository.findById(studentId).orElse(null);

        if(s != null){
            s.setName(student.getName());
            studentRepository.save(s);
            return "執行資料庫的 Update 操作";
        }else{
            return "Update Error";
        }
    }


//3.刪除
    @DeleteMapping("/students/{studentId}")
    public String delete(@PathVariable Integer studentId){

        studentRepository.deleteById(studentId);

        return "執行資料庫的 Delete 操作";
    }

//4.查詢
.orElse(null):意思是指如果查詢不到資料，student的值就是null

    @GetMapping("/students/{studentId}")
    public Student read(@PathVariable Integer studentId){

        Student student = studentRepository.findById(studentId).orElse(null);

        return student;
    }

//四.遵循Spring Data JPA的命名規則，自定義查詢條件
findByXxx的命名規則

如果想要使用不同的查詢條件，只要在StudentRepository中依照命名規則來新增方法即可
  
public interface StudentRepository extends CrudRepository<Student,Integer> {
    //返回值可以任意;意思是用name來查詢 ex:select * from Student where name = ? 
    List<Student> findByName(String name);
    //返回值可以任意;意思是依序用Id和Name查詢
    Student findByIdAandName(Integer id, String name);
}

//五.原生SQL查詢Query
@Query
目的:用來解決findByXxx無法寫出"複雜的查詢邏輯"的問題
用途:在Spring Data JPA中，"執行原生SQL語法"
優先使用findByXxx的命名規則，"複雜的邏輯"才使用@Query

public interface StudentRepository extends CrudRepository<Student,Integer> {
    //?1與?2，在問號後面加上數字，指定要載入第幾個參數的值
    //nativeQuery，true=一般SQL語法;false=JPQL
    @Query(value = "SELECT id, name FROM student WHERE id = ?1 AND name = ?2",nativeQuery = true)
    Student test1(Integer id, String name);

}
