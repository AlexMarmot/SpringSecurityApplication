package com.example.springsecurityapplication.controllers;

import com.example.springsecurityapplication.models.Person;
import com.example.springsecurityapplication.repositories.ProductRepository;
import com.example.springsecurityapplication.security.PersonDetails;
import com.example.springsecurityapplication.services.PersonService;
import com.example.springsecurityapplication.services.ProductService;
import com.example.springsecurityapplication.util.PersonValidator;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class MainController {
    private final ProductRepository productRepository;

    private final PersonValidator personValidator;
    private final PersonService personService;

    private final ProductService productService;

    public MainController(ProductRepository productRepository, PersonValidator personValidator, PersonService personService, ProductService productService) {
        this.productRepository = productRepository;
        this.personValidator = personValidator;
        this.personService = personService;
        this.productService = productService;
    }

    @GetMapping("/person_account")
    public String index(Model model){
        // Получаем объект аутентификации -> с помощью SpringContextHolder обращаемся к контексту и на нем вызываем метод аутентификации. Из сессии текущего пользователя получаем объект, который был положен в данную сессию после аутентификации пользователя
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PersonDetails personDetails = (PersonDetails) authentication.getPrincipal();
        String role = personDetails.getPerson().getRole();
        System.out.println(personDetails.getPerson());
        System.out.println("ID пользователя: " + personDetails.getPerson().getId());
        System.out.println("Логин пользователя: " + personDetails.getPerson().getLogin());
        System.out.println("Пароль пользователя: " + personDetails.getPerson().getPassword());
        System.out.println(personDetails);
        if(role.equals("ROLE_ADMIN")){
            return "redirect:/admin";
        }
        model.addAttribute("products", productService.getAllProduct());
        return "/user/index";
    }

    //    @GetMapping("/registration")
//    public String registration(Model model){
//        model.addAttribute("person", new Person());
//        return "registration";
//    }
    // Можно заменить следующим кодом:
    @GetMapping("/registration")
    public String registration(@ModelAttribute("person") Person person){
        return "registration";
    }

    @PostMapping("/registration")
    public String resultRegistration(@ModelAttribute("person") @Valid Person person, BindingResult bindingResult){
        personValidator.validate(person, bindingResult);
        if (bindingResult.hasErrors()){
            return "registration";
        }
        personService.register(person);
        return "redirect:/person_account";

    }

    @GetMapping("/person_account/product/info/{id}")
    public String infoProduct(@PathVariable("id") int id, Model model){
        model.addAttribute("product", productService.getProductId(id));
        return "/user/infoProduct";
    }

    @PostMapping("/person_account/product/search")
    public String productSearch(@RequestParam("search") String search, @RequestParam("ot") String ot, @RequestParam("do") String Do, @RequestParam(value = "price", required = false, defaultValue = "") String price, @RequestParam(value = "contract", required = false, defaultValue = "")String contract, Model model){
        model.addAttribute("products", productService.getAllProduct());

        if(!ot.isEmpty() & !Do.isEmpty()){
            if(!price.isEmpty()){
                if(price.equals("sorted_by_ascending_price")) {
                    if (!contract.isEmpty()) {
                        if (contract.equals("furniture")) {
                            model.addAttribute("search_product", productRepository.findByTitleAndCategoryOrderByPriceAsc(search.toLowerCase(), Float.parseFloat(ot), Float.parseFloat(Do), 1));
                        } else if (contract.equals("appliances")) {
                            model.addAttribute("search_product", productRepository.findByTitleAndCategoryOrderByPriceAsc(search.toLowerCase(), Float.parseFloat(ot), Float.parseFloat(Do), 3));
                        } else if (contract.equals("clothes")) {
                            model.addAttribute("search_product", productRepository.findByTitleAndCategoryOrderByPriceAsc(search.toLowerCase(), Float.parseFloat(ot), Float.parseFloat(Do), 2));
                        }
                    } else {
                        model.addAttribute("search_product", productRepository.findByTitleOrderByPriceAsc(search.toLowerCase(), Float.parseFloat(ot), Float.parseFloat(Do)));
                    }
                } else if(price.equals("sorted_by_descending_price")){
                    if(!contract.isEmpty()){
                        System.out.println(contract);
                        if(contract.equals("furniture")){
                            model.addAttribute("search_product", productRepository.findByTitleAndCategoryOrderByPriceDesc(search.toLowerCase(), Float.parseFloat(ot), Float.parseFloat(Do), 1));
                        }else if (contract.equals("appliances")) {
                            model.addAttribute("search_product", productRepository.findByTitleAndCategoryOrderByPriceDesc(search.toLowerCase(), Float.parseFloat(ot), Float.parseFloat(Do), 3));
                        } else if (contract.equals("clothes")) {
                            model.addAttribute("search_product", productRepository.findByTitleAndCategoryOrderByPriceDesc(search.toLowerCase(), Float.parseFloat(ot), Float.parseFloat(Do), 2));
                        }
                    }  else {
                        model.addAttribute("search_product", productRepository.findByTitleOrderByPriceDesc(search.toLowerCase(), Float.parseFloat(ot), Float.parseFloat(Do)));
                    }
                }
            } else {
                model.addAttribute("search_product", productRepository.findByTitleAndPriceGreaterThanEqualAndPriceLessThanEqual(search.toLowerCase(), Float.parseFloat(ot), Float.parseFloat(Do)));
            }
        } else {
            model.addAttribute("search_product", productRepository.findByTitleContainingIgnoreCase(search));
        }

        model.addAttribute("value_search", search);
        model.addAttribute("value_price_ot", ot);
        model.addAttribute("value_price_do", Do);
        return "/product/product";
    }

}
