package com.example.demo.controller;

import com.example.demo.entity.NhanVien;
import com.example.demo.services.NhanVienService;
import com.example.demo.services.PhongBanService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@org.springframework.stereotype.Controller
@RequestMapping("/books")
public class Controller {
    @Autowired
    private NhanVienService bookService;
    @Autowired
    private PhongBanService categoryService;
    @GetMapping
    public String showAllBooks(Model model){
        List<NhanVien> books = bookService.getAllBooks();
        model.addAttribute("books",books);
        return "book/list";
    }
    @GetMapping("/search")
    public String search(@RequestParam("searchText") String searchText,Model model) {
        List<NhanVien> books = bookService.getAllBooks();
        List<NhanVien> filteredBooks = new ArrayList<>();

        if (searchText != null && !searchText.isEmpty()) {
            filteredBooks = books.stream()
                    .filter(book -> book.getTitle().contains(searchText))
                    .collect(Collectors.toList());
        }
        model.addAttribute("books", filteredBooks);
        return "book/list";
    }
    @GetMapping("/add")
    public String addBookForm(Model model){
        model.addAttribute("book", new NhanVien());
        model.addAttribute("categories",categoryService.getAllCategories());
        return "book/add";
    }
    @PostMapping("/add")
    public String addBook(@Valid @ModelAttribute("book") NhanVien book, BindingResult result, Model model){
        // check lỗi
        if(result.hasErrors()){
            model.addAttribute("categories",categoryService.getAllCategories());
            return "book/add";
        }
            bookService.addBook(book);
            return "redirect:/books";
        }

    @GetMapping("/delete/{id}")
    public String deleteBook(@PathVariable("id") Long id){
        bookService.deleteBook(id);
        return "redirect:/books";
    }
    @GetMapping("/edit/{id}")
    public String editBookForm(@PathVariable("id") Long id,Model model){
        NhanVien editBook = bookService.getBookId(id);
        if(editBook!=null){
            model.addAttribute("book",editBook);
            model.addAttribute("categories",categoryService.getAllCategories());
            return "book/edit";
        }
        else{
            return "redirect:/books";
        }
    }
    @PostMapping("/edit/{id}")
    public String edit(@PathVariable("id")Long id, @ModelAttribute("book") NhanVien editBook, BindingResult result, Model model){
        // check lỗi
        if(result.hasErrors()){
            model.addAttribute("categories",categoryService.getAllCategories());
            return "book/edit";
        }
        else {
            NhanVien existingBook = bookService.getBookId(id);
            if (existingBook != null){
                existingBook.setAuthor(editBook.getAuthor());
                existingBook.setTitle(editBook.getTitle());
                existingBook.setPrice(editBook.getPrice());
                existingBook.setCategory((editBook.getCategory()));
                bookService.updateBook(existingBook);
            }
            return "redirect:/books";
        }
    }

}
