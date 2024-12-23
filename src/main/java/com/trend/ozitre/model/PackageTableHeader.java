package com.trend.ozitre.model;

import com.trend.ozitre.util.ConstantUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PackageTableHeader {
    String packageName = ConstantUtil.PACKAGE_TABLE_PNAME;
    String month = ConstantUtil.PACKAGE_TABLE_MONTH;
    String installment = ConstantUtil.PACKAGE_TABLE_INSTALLMENT;
    String price = ConstantUtil.PACKAGE_TABLE_PRICE;
}
