package io.spring.application.item;

import io.spring.application.ItemQueryService;
import io.spring.core.item.Item;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

class DuplicatedItemValidator implements ConstraintValidator<DuplicatedItemConstraint, String> {

  @Autowired private ItemQueryService itemQueryService;

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    return !itemQueryService.findBySlug(Item.toSlug(value), null).isPresent();
  }
}
