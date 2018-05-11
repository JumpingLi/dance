package com.champion.dance.utils.page;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.io.IOException;
import java.util.Map;

/**
 * Created by lihongpeng on 7/17/2017.
 */
public class PaginationListJsonSerializer extends JsonSerializer<PaginationList> {
    private ObjectMapper mapper;

    public PaginationListJsonSerializer(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void serialize(PaginationList value, JsonGenerator gen, SerializerProvider serializers) throws IOException {

        final Map<String, Object> paginationMap = Maps.newHashMap();
        final Paginator paginator = value.getPaginator();
        paginationMap.put("totalCount", paginator.getTotalCount());
        paginationMap.put("totalPages", paginator.getTotalPages());
        paginationMap.put("page", paginator.getPage());
        paginationMap.put("limit", paginator.getLimit());
        paginationMap.put("items", Lists.newArrayList(value));

        paginationMap.put("startRow", paginator.getStartRow());
        paginationMap.put("endRow", paginator.getEndRow());

        paginationMap.put("offset", paginator.getOffset());

        paginationMap.put("slider", paginator.getSlider());

        paginationMap.put("prePage", paginator.getPrePage());
        paginationMap.put("nextPage", paginator.getNextPage());

        paginationMap.put("firstPage", paginator.isFirstPage());
        paginationMap.put("hasNextPage", paginator.isHasNextPage());
        paginationMap.put("hasPrePage", paginator.isHasPrePage());
        paginationMap.put("lastPage", paginator.isLastPage());

        mapper.writeValue(gen, paginationMap);
    }
}
