package com.project.mapdata.repository;

import com.example.adopt.domain.MapData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MapDataRepository extends JpaRepository<MapData, Long> {

    // 장소명으로 검색(부분검색)
    List<MapData> findByPlaceNameContainingIgnoreCase(String placeName);

    // 주소로 검색
    List<MapData> findByAddressContainingIgnoreCase(String address);
}