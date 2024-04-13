package edu.kalinin.acc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import edu.kalinin.acc.entity.PostingView;

public interface PostingViewRepository extends JpaRepository<PostingView, String> {
}