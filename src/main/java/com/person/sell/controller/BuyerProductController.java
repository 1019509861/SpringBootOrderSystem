package com.person.sell.controller;

import com.person.sell.VO.ProductInfoVO;
import com.person.sell.VO.ProductVO;
import com.person.sell.VO.ResultVO;
import com.person.sell.dataobject.ProductCategory;
import com.person.sell.dataobject.ProductInfo;
import com.person.sell.service.CategoryService;
import com.person.sell.service.ProductInfoService;
import com.person.sell.utils.ResultVOUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/buyer/product")
public class BuyerProductController {

    @Autowired
    private ProductInfoService productInfoService;
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/list")
    public ResultVO list(){

        //1. 查询所有上架商品
        List<ProductInfo> productInfoList = productInfoService.findUpAll();

        //2. 查询类目（一次性查询）
        //List<Integer> categoryList = new ArrayList<>();
        //遍历商品类目ID
        /*  2.1：传统
            for (ProductInfo p:productInfoList) {
                categoryList.add(p.getCategoryType());
            }
        */
        //2.2：java8 lambda 表达式
        List<Integer> categoryList = productInfoList.stream()
                                    .map(e -> e.getCategoryType())
                                    .collect(Collectors.toList());
        List<ProductCategory> productCategoryList = categoryService.findByCategoryTypeIn(categoryList);

        //3. 数据拼装
        List<ProductVO> productVOList = new ArrayList<>();
        for(ProductCategory productCategory:productCategoryList){
            //遍历商品类目
            ProductVO productVO = new ProductVO();
            productVO.setCategoryName(productCategory.getCategoryName());
            productVO.setCategoryType(productCategory.getCategoryType());
            //遍历商品类目下的商品
            List<ProductInfoVO> productInfoVOList = new ArrayList<>();
            for(ProductInfo productInfo : productInfoList){
                if(productInfo.getCategoryType().equals(productCategory.getCategoryType())){
                    ProductInfoVO productInfoVO = new ProductInfoVO();
                    //拷贝值，将 info 中的属性 copy到 infoVO 中
                    BeanUtils.copyProperties(productInfo,productInfoVO);
                    productInfoVOList.add(productInfoVO);
                }
            }
            productVO.setProductInfoVOList(productInfoVOList);
            productVOList.add(productVO);
        }

        return ResultVOUtil.success(productVOList);
    }


}
