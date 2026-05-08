package com.impactovisible.controller;

import com.impactovisible.dto.RankingDTO;
import com.impactovisible.service.RankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ranking")
@RequiredArgsConstructor
public class RankingController {

  private final RankingService rankingService;

  @GetMapping
  public List<RankingDTO> getRanking() {
    return rankingService.obtenerRanking();
  }
}
