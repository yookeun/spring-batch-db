package books;

import org.springframework.batch.item.ItemProcessor;

/**
 * 비즈니스 로직 클래스 
 * 
 * Item Reader에 읽어 들인 Item에 대하여 필요한 로직처리 작업을 수행한다. 
 * BookItemProcessor 클래스는 ItemProcessor 인터페이스를 구현한다.
 * 이 클래스는 우리가 원하는 로직으로 변경후에 batch job안에 그 변경된 객체를 전달한다.   
 * 이 클래스에서는 파일로 넘겨진 Book.csv를 받고  Book라는 객체를 받고, 책 가격을 일괄적으로 10% 인상하여 DB에 인서트하는 방법을 사용한다. 
 * @author ykkim
 *
 */
public class BookItemProcessor implements ItemProcessor<Book, Book> {
	public Book process(final Book book) throws Exception {		
		//모든 책값을 10%인상한다.
		final double bookPrice  = book.getBookPrice() + (book.getBookPrice() * 0.1);
		final Book changedBook = new Book();
		changedBook.setBookName(book.getBookName());
		changedBook.setBookAuthor(book.getBookAuthor());
		changedBook.setBookPrice(bookPrice);
		System.out.println("Convering (" + book + ") into (" + changedBook + ")");
		return changedBook;
	}

}
