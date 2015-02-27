package books;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * 배치 실행클래스 
 * @author ykkim
 *
 */
@Configuration
@EnableBatchProcessing
public class BatchConfiguration {
	
	
	/**
	 * mysql or mariadb 에 연결할 bean 객체로 만든다. 
	 * @return
	 */
	@Bean(destroyMethod="close")
	public DataSource dataSource() {
		BasicDataSource dataSource = new BasicDataSource();		
		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		dataSource.setUrl("jdbc:mysql://localhost:3306/testdb?characterEncoding=UTF-8");
		dataSource.setUsername("root");
		dataSource.setPassword("1234");
		dataSource.setRemoveAbandoned(true);
		dataSource.setRemoveAbandonedTimeout(30);		
		return dataSource;
	}
	
	
	/**
	 * csv파일을 읽어들인다. 각 라인별로 읽어들여 Book객체로 전환한다.
	 * @return
	 */
	@Bean
	public ItemReader<Book> reader() {
		FlatFileItemReader<Book> reader = new FlatFileItemReader<Book>();
		reader.setResource(new ClassPathResource("book.csv"));   //src/main/resources 안에 위치한다.
		//각 라인을 읽어들여 Book객체와 매핑한다.
		reader.setLineMapper(new DefaultLineMapper<Book>(){{			
			setLineTokenizer(new DelimitedLineTokenizer() {{
				setNames(new String[] {"bookName", "bookAuthor","bookPrice"});
			}});
			setFieldSetMapper(new BeanWrapperFieldSetMapper<Book>() {{
				setTargetType(Book.class);
			}});
		}});
		return reader;
	}
	
	
	/**
	 * Book에 대한 처리가 있는 BookItemProcessor 클래스를  생성한다.
	 * ItemProcessor<Book, Book> Book으로 Input하고 Book으로 Output한다. 
	 * @return
	 */
	@Bean
	public ItemProcessor<Book, Book> processor() {
		return new BookItemProcessor();
	}
	
	
	/**
	 * 실제 db에 인서트하는 로직 
	 * @param dataSource
	 * @return
	 */
	@Bean
	public ItemWriter<Book> writer(DataSource dataSource) {		
		JdbcBatchItemWriter<Book> writer = new JdbcBatchItemWriter<Book>();
		writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<Book>());
		writer.setSql("INSERT INTO book(bookName, bookAuthor, bookPrice, registDate) VALUES(:bookName, :bookAuthor, :bookPrice, NOW())");		
		writer.setDataSource(dataSource);
		return writer;
	}
	
	/**
	 * 하나의 배치작업을 정의한 Job instance를 생성하고 수행한다.  
	 * 배치가 실행될때마다 BATCH_JOB_INSTANCE 테이블에 JOB_NAME에  "importBookJob"로 레코드가 생성된다.     
	 * @param jobs
	 * @param s1
	 * @return
	 */
	@Bean
	public Job importBookJob(JobBuilderFactory jobs, Step s1) {
		return jobs.get("importBookJob")
				.incrementer(new RunIdIncrementer())
				.flow(s1)
				.end()
				.build();
	}
	
	/**
	 * 스프링에서 Batch job은 Step의 모음으로 구성되어 있다. 
	 * 아래 스탭은 csv파일을 읽고(reader), 로직을 처리하고(processor), 다시 db에 저장하는 과정(write)을 수행한다. 
	 * job은 하나이상의 step으로 구성된다. 
	 * chunk(10)은 10개의  item에 대한 처리를 하고 commit를 한다.
	 * @param stepBuildFactory
	 * @param reader
	 * @param writer
	 * @param processor
	 * @return
	 */
	@Bean
	public Step step1(StepBuilderFactory stepBuildFactory, ItemReader<Book> reader, ItemWriter<Book> writer, ItemProcessor<Book, Book> processor) {
		return stepBuildFactory.get("step1").<Book, Book> chunk(10)
				.reader(reader)
				.processor(processor)
				.writer(writer)
				.build();
	}
	
	
	@Bean
	public JdbcTemplate jdbcTemplate(DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}

}
