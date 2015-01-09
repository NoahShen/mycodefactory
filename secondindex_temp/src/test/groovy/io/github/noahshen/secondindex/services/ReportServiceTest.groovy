package io.github.noahshen.secondindex.services

import groovy.sql.Sql
import io.github.noahshen.nagrand.Nagrand
import io.github.noahshen.secondindex.entity.ReportEntity

import java.util.logging.Level

/**
 * Created by noahshen on 15-1-8.
 */
class ReportServiceTest extends GroovyTestCase {
    Nagrand nagrand
    Sql sql
    String tableName = ReportEntity.simpleName

    ReportService reportService

    void setUp() {
        sql = Sql.newInstance("jdbc:hsqldb:mem:database", "sa", "", "org.hsqldb.jdbc.JDBCDriver")
        nagrand = new Nagrand(sql)
        nagrand.enableQueryLogging(Level.INFO)
        nagrand.register(ReportEntity, true)

        reportService = []
    }

    void tearDown() {
        sql.execute("drop table ${tableName} if exists".toString())
        sql.close()
    }

    void testAddReportEntity() {
        ReportEntity entity = [yymm: 1409, initialBalance: 100.0G, apAmount: 87G, ]
        reportService.addReportEntity(entity)

        def result = sql.rows("select * from ${tableName}".toString())

        assert result.size() == 1
        assert result.first().initialBalance == 100.0G
    }

    void testPaginateReportEntity() {
        15.times {
            ReportEntity entity = [yymm: "1409", initialBalance: 100.0G, apAmount: it,]
            reportService.addReportEntity(entity)
        }

        def entities = reportService.paginateReportEntity("1409", 1, 10)
        assert entities.size() == 10
        assert entities.first().apAmount == 0

        def entities2 = reportService.paginateReportEntity("1409", 2, 10)
        assert entities2.size() == 5
        assert entities2.first().apAmount == 10
    }


    void testCreateMonthCountIndex() {
        def allMonth = ["1409", "1410", "1411"]
        allMonth.each { month ->
            15.times {
                ReportEntity entity = [yymm: month, initialBalance: 100.0G, apAmount: it + 87,]
                reportService.addReportEntity(entity)
            }
        }
        reportService.createMonthCountIndex(allMonth)

        assert reportService.monthCountIndex.size() == 3
        reportService.monthCountIndex.each { month, count ->
            assert count == 15
        }
    }

    void testCreateOffsetCountIndex() {
        def allMonth = ["1409", "1410", "1411"]
        allMonth.each { month ->
            15.times {
                ReportEntity entity = [yymm: month, initialBalance: 100.0G, apAmount: it + 87,]
                reportService.addReportEntity(entity)
            }
        }

        def offsets = [1, 10, 15]
        reportService.createOffsetCountIndex(offsets)

        assert reportService.offsetCountIndex.size() == 3
        reportService.offsetCountIndex.each { offset, id ->
            if (offset == 1) {
                assert id == 1
            }
            if (offset == 10) {
                assert id == 10
            }
            if (offset == 15) {
                assert id == 15
            }
        }
    }


    void testPaginateReportEntityBySecondIndex() {
        def allMonth = ["1409", "1410", "1411"]
        allMonth.each { month ->
            11.times {
                ReportEntity entity = [yymm: month, initialBalance: 100.0G, apAmount: it + 87,]
                reportService.addReportEntity(entity)
            }
        }
        reportService.createMonthCountIndex(allMonth)
        def offsets = [10, 20, 30]
        reportService.createOffsetCountIndex(offsets)

        def entities = reportService.paginateReportEntityBySecondIndex("1410", 1, 5)
        assert entities.size() == 5

        def entities2 = reportService.paginateReportEntity("1410", 1, 5)
        assert entities2.size() == 5

        assert entities == entities2

    }
}
