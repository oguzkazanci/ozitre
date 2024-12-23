package com.trend.ozitre.model;

import com.itextpdf.kernel.color.Color;
import com.itextpdf.layout.element.Image;
import com.trend.ozitre.util.ConstantUtil;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class HeaderDetails {
    String invoiceTitle= ConstantUtil.INVOICE_TITLE;
    String registryTitle= ConstantUtil.REGISTRY_TITLE;
    String invoiceNoText=ConstantUtil.INVOICE_NO_TEXT;
    String invoiceDateText=ConstantUtil.INVOICE_DATE_TEXT;
    Image imageBanner;
    String invoiceNo=ConstantUtil.EMPTY;
    String invoiceDate=ConstantUtil.EMPTY;
    Color borderColor=Color.GRAY;
}
