package com.widget.storage.contract;

import com.widget.storage.abstraction.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PageResponse<T> {
    private List<T> records;
    private int total;

    /**
     * Creates paged response based on records collection.
     * @param records Records.
     * @param pageRequest Page request.
     */
    public static PageResponse create(List records, PageRequest pageRequest) {
        int pageStartIndex = pageRequest.getPage() * pageRequest.getPerPage(),
                pageEndIndex = Math.min((pageStartIndex + pageRequest.getPerPage()), records.size());
        return pageStartIndex > pageEndIndex
            ? new PageResponse(new ArrayList<>(), records.size())
            : new PageResponse(records.subList(pageStartIndex, pageEndIndex), records.size());
    }

    /**
     * Returns empty page request.
     * @return
     */
    public static PageResponse empty() {
        return new PageResponse(new ArrayList<>(), 0);
    }
}
