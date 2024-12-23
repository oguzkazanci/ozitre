package com.trend.ozitre.model;

import com.itextpdf.kernel.color.Color;
import com.trend.ozitre.util.ConstantUtil;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressDetails {
    private String studentInfoText= ConstantUtil.STUDENT_INFO;
    private String studentFullNameText=ConstantUtil.STUDENT_FULLNAME;
    private String studentFullName=ConstantUtil.STUDENT_FULLNAME;
    private String studentParentText=ConstantUtil.STUDENT_PARENT_TEXT;
    private String studentParent=ConstantUtil.STUDENT_PARENT;

    private String invoiceEditDateText=ConstantUtil.EDIT_DATE_TEXT;
    private String registryDateText=ConstantUtil.REGISTRY_DATE_TEXT;
    private String invoiceEditDate=ConstantUtil.EDIT_DATE_TEXT;
    private Color borderColor=Color.GRAY;
    }
