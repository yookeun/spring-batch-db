package books;



import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;



@ComponentScan
@EnableAutoConfiguration
public class BookMain {
	public static void main(String[] args) {
		//실제 배치 실행구문 
		ApplicationContext ctx = SpringApplication.run(BookMain.class, args);
		
		//입력된 결과 확인부분 (배치랑 상관없음)
        List<Book> results = ctx.getBean(JdbcTemplate.class).query("SELECT  bookName, bookAuthor, bookPrice FROM book", new RowMapper<Book>() {           
            public Book mapRow(ResultSet rs, int row) throws SQLException {
            	Book book = new Book();
            	book.setBookName(rs.getString(1));
            	book.setBookAuthor(rs.getString(2));            	
            	book.setBookPrice(rs.getDouble(3));
                return book;
            }
        });

        if (results ==null || results.size() == 0) {
        	System.out.println("book empty!!!");
        } else {
            for (Book book : results) {
                System.out.println("Found <" + book + "> in the database.");
            }
        }
	}	
}
