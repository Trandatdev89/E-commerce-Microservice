package com.project01.productservice.repository.criteria;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchCriteria {
     private String key;
     private SearchOperation operator;
     private Object value;


     public SearchCriteria(String key, String operators, Object value,String prefix,String suffix) {
          SearchOperation searchOperation= SearchOperation.getSimpleOperation(operators.charAt(0));
          if (searchOperation != null) {
               if(searchOperation == SearchOperation.EQUALITY) {
                    boolean isPrefix = prefix != null && prefix.equals(SearchOperation.ZERO_OR_MORE_REGEX);
                    boolean isSubfix = suffix != null && suffix.equals(SearchOperation.ZERO_OR_MORE_REGEX);
                    if (isPrefix) {
                         searchOperation = SearchOperation.ENDS_WITH;
                    }
                    else if (isSubfix) {
                         searchOperation = SearchOperation.STARTS_WITH;
                    }else if (isPrefix && isSubfix){
                         searchOperation = SearchOperation.CONTAINS;
                    }
               }
          }
          this.key = key;
          this.operator = searchOperation;
          this.value = value;
     }
}
