package com.trend.ozitre.model;

import com.trend.ozitre.util.ConstantUtil;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductTableHeader {
    String lesson = ConstantUtil.PRODUCT_TABLE_LESSON;
    String teacher = ConstantUtil.PRODUCT_TABLE_TEACHER;
    String date = ConstantUtil.PRODUCT_TABLE_DATE;
    String price = ConstantUtil.PRODUCT_TABLE_PRICE;

    String days = ConstantUtil.PRODUCT_TABLE_DAYS;
}
