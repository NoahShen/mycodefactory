package io.github.noahshen.secondindex.entity

import io.github.noahshen.nagrand.annotation.AutoDateCreated
import io.github.noahshen.nagrand.annotation.AutoLastUpdated
import io.github.noahshen.nagrand.annotation.Entity
import io.github.noahshen.nagrand.annotation.Id

/**
 * Created by noahshen on 15-1-8.
 */
@Entity
class ReportEntity {

    @Id
    Integer reportId;

    String yymm;

    BigDecimal initialBalance

    BigDecimal apAmount

    @AutoDateCreated
    Date addTime

    @AutoLastUpdated
    Date updateTime
}
