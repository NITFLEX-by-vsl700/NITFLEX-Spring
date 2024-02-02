package com.vsl700.nitflex.controllers;

import com.vsl700.nitflex.models.dto.MovieDTO;
import com.vsl700.nitflex.repo.MovieRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private ModelMapper modelMapper;

    @GetMapping
    public String index(Model model){
        List<MovieDTO> movies = movieRepository.findAll().stream().map(m -> modelMapper.map(m, MovieDTO.class))
                .toList();
        model.addAttribute("movies", movies);
        return "index";
    }
}