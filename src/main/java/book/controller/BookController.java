package book.controller;

import book.domain.dto.BookDTO;
import book.domain.dto.RecommendDTO;
import book.domain.dto.UserDTO;
import book.domain.exception.BusinessException;
import book.domain.result.BaseResult;
import book.service.BookInfoService;
import book.service.RecommendService;
import book.util.ExceptionHandler;
import book.util.LoggerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * @author yutong song
 * @date 2018/3/20
 */
@Controller
public class BookController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BookController.class);

    @Resource(name = "bookInfoService")
    private BookInfoService bookInfoService;

    @Resource(name = "recommendService")
    private RecommendService recommendService;

    @RequestMapping(value = "/reader/index", method = RequestMethod.GET)
    public String listAllBooks(HttpSession httpSession, Model model) {
        BaseResult result=new BaseResult();
        try {
            LoggerUtil.info(LOGGER, "enter in BookController[listAllBooks]");
            UserDTO userDTO = (UserDTO) httpSession.getAttribute("isLogin");
            if (userDTO == null) {
                return "redirect:/login";
            }
            List<BookDTO> bookDTOList = bookInfoService.listAllBooks();
            //获取推荐列表
            List<RecommendDTO> recommendDTOList=recommendService.listByUserId(userDTO.getUserId());
            model.addAttribute("userDTO", userDTO);
            model.addAttribute("bookList", bookDTOList);
            model.addAttribute("recommendDTOList",recommendDTOList);
            result.setSuccess(true);
            return "reader_index";
        }catch (BusinessException be){
            ExceptionHandler.handleBusinessException(LOGGER,result,be,"展示书籍失败");
        }catch (Exception ex){
            ExceptionHandler.handleSystemException(LOGGER,result,ex,"展示书籍失败");
        }
        return "error";
    }

    /**
     * 根据书名或者作者查询
     * @param httpSession
     * @param model
     * @param condition
     * @return
     */
    @RequestMapping(value = "/reader/search",method = RequestMethod.POST)
   public String listBooksByConditions(HttpSession httpSession,Model model,String condition){
        BaseResult result=new BaseResult();
        try {
            LoggerUtil.info(LOGGER, "enter in BookController[listBooksByConditions],condition:{0}", condition);
            UserDTO userDTO = (UserDTO) httpSession.getAttribute("isLogin");
            if (userDTO == null) {
                return "redirect:/login";
            }
            List<BookDTO> bookDTOList = bookInfoService.listBooksByConditions(condition);
            model.addAttribute("userDTO", userDTO);
            model.addAttribute("bookList", bookDTOList);
            result.setSuccess(true);
            return "reader_index";
        }catch (BusinessException be){
            ExceptionHandler.handleBusinessException(LOGGER,result,be,"根据书名或者作者查询书籍失败,condition:{0}",condition);
        }catch (Exception ex){
            ExceptionHandler.handleSystemException(LOGGER,result,ex,"根据书名或者作者查询书籍失败,condition:{0}",condition);
        }
        return "error";
   }

    /**
     * 根据书名和作者显示书籍详情
     * @param httpSession
     * @param model
     * @param bookName
     * @param author
     * @return
     */
   @RequestMapping(value = "/book/{bookName}/{author}")
   public String listDetail(HttpSession httpSession, Model model, @PathVariable String bookName,@PathVariable String author){
        LoggerUtil.info(LOGGER,"enter in BookController[listDetail],bookName:{0},author:{1}",bookName,author);
        BaseResult result=new BaseResult();
        try {
            UserDTO userDTO = (UserDTO) httpSession.getAttribute("isLogin");
            if (userDTO == null) {
                return "redirect:/login";
            }
            List<BookDTO> bookDTOList = bookInfoService.listBooksByNameAndAuthor(bookName, author);
            List<RecommendDTO> recommendDTOList = recommendService.listByUserId(userDTO.getUserId());
            model.addAttribute("recommendDTOList",recommendDTOList);
            model.addAttribute("userDTO", userDTO);
            model.addAttribute("bookList", bookDTOList);
            result.setSuccess(true);
            return "reader_detail";
        }catch (BusinessException be){
            ExceptionHandler.handleBusinessException(LOGGER,result,be,"查询书籍详情失败,bookName:{0},author:{1}",bookName,author);
        }catch (Exception ex){
            ExceptionHandler.handleSystemException(LOGGER,result,ex,"查询书籍详情失败,bookName:{0},author:{1}",bookName,author);
        }
        return "error";
   }

    /**
     * 根据出版社，介绍，作者，位置进行复合搜索
     * @param httpSession
     * @param model
     * @param publisher
     * @param introduction
     * @param author
     * @param location
     * @return
     */
    public String queryByMultiConditions(HttpSession httpSession,Model model,@PathVariable String publisher,@PathVariable String introduction,@PathVariable String author,@PathVariable String location) {
        LoggerUtil.info(LOGGER, "enter in BookController[queryByMultiConditions],publisher:{0} introduction {1} author:{2} location:{3}", publisher, introduction, author, location);
        BaseResult result = new BaseResult();
        try {
            UserDTO userDTO = (UserDTO) httpSession.getAttribute("isLogin");
            if (userDTO == null) {
                return "redirect:/login";
            }
            List<BookDTO> bookDTOList = bookInfoService.queryByMultiConditions(publisher, introduction, author, location);
            model.addAttribute("bookDTOList", bookDTOList);
            result.setSuccess(true);
            return "reader_index";
        } catch (BusinessException be) {
            ExceptionHandler.handleBusinessException(LOGGER, result, be, "复合搜索书籍失败,publisher:{0} introduction {1} author:{2} location:{3}", publisher, introduction, author, location);
        } catch (Exception ex) {
            ExceptionHandler.handleSystemException(LOGGER, result, ex, "复合搜索书籍失败,publisher:{0} introduction {1} author:{2} location:{3}", publisher, introduction, author, location);
        }
        return "error";
    }
}
