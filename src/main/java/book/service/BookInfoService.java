package book.service;

import book.domain.dto.BookDTO;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * @author yutong song
 * @date 2018/3/20
 */
public interface BookInfoService {

    List<BookDTO>listAllBooks();

    List<BookDTO>AdminListAllBooks();

    PageInfo<BookDTO> AdminListAllBooks(int page);

    List<BookDTO> listBooksByConditions(String conditions);

    List<BookDTO> listBooksByNameAndAuthor(String bookName,String author);

    boolean updateBookByBookId(BookDTO bookDTO);

    boolean insertBook(BookDTO bookDTO);

    boolean deleteBook(long bookId);

    List<BookDTO> queryByMultiConditions(String publisher,String introduction,String author,String location);
}
