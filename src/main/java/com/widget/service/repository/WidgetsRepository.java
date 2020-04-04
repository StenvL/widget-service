package com.widget.service.repository;

import com.widget.service.model.Widget;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface WidgetsRepository extends PagingAndSortingRepository<Widget, Long> {
    @Query(
        value = "select * from #{#entityName} w where " +
            "w.x - w.width / 2 >= ?1 and w.y - w.height / 2 >= ?2 " +
            "and w.x + w.width / 2 <= ?3 and w.y + w.height / 2 <= ?4",
        countQuery = "select count(*) from #{#entityName} w where " +
            "w.x - w.width / 2 >= ?1 and w.y - w.height / 2 >= ?2 " +
            "and w.x + w.width / 2 <= ?3 and w.y + w.height / 2 <= ?4",
        nativeQuery = true
    )
    Page<Widget> findByArea(int x1, int y1, int x2, int y2, Pageable pageable);

    @Query("select count(*) from #{#entityName} where z = ?1")
    int getWidgetsCountByZIndex(int zIndex);

    @Query("select max(z) from #{#entityName}")
    Integer getMaxZIndex();

    @Query("select min(z) from #{#entityName}")
    Integer getMinZIndex();

    @Modifying
    @Query("update #{#entityName} set z = z + 1 where z >= ?1")
    void incZIndices(int startIndex);
}
