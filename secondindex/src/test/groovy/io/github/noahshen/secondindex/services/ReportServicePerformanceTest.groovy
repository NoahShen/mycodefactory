package io.github.noahshen.secondindex.services
import groovy.sql.Sql
import io.github.noahshen.nagrand.Nagrand
import io.github.noahshen.nagrand.builders.SQLDialect
import io.github.noahshen.secondindex.entity.ReportEntity
/**
 * Created by noahshen on 15-1-8.
 */
class ReportServicePerformanceTest extends GroovyTestCase {
    Nagrand nagrand
    Sql sql
    String tableName = ReportEntity.simpleName

    ReportService reportService

    void setUp() {
        sql = Sql.newInstance("jdbc:hsqldb:mem:database", "sa", "", "org.hsqldb.jdbc.JDBCDriver")
        nagrand = new Nagrand(sql, SQLDialect.HSQLDB)
        nagrand.register(ReportEntity, true)

        reportService = []
    }

    void tearDown() {
        sql.execute("drop table ${tableName}".toString())
        sql.close()
    }

    void testPaginateReportEntity() {
        def allMonth = ["1409", "1410", "1411", "1412", "1501"]
        def countOfMonth = 11111
        def offsets = []
        1000.step(50000, 1000) {
            offsets << it
        }

        Integer totalCount = 0
        println("start add record")
        def addRecordStartTime = System.currentTimeMillis()
        allMonth.each { month ->
            countOfMonth.times {
                ReportEntity entity = [yymm: month, initialBalance: 100.0G, apAmount: 87,]
                reportService.addReportEntity(entity)
                totalCount++
            }
        }
        println("addRecord complete! total: ${totalCount}, escaped:${(System.currentTimeMillis() - addRecordStartTime) / 1000} sec")

        println("start generateSecondIndex")
        def generateSecondIndexTime = System.currentTimeMillis()
        reportService.createMonthCountIndex(allMonth)
        reportService.createOffsetCountIndex(offsets)
        println("addRecord escaped:${(System.currentTimeMillis() - generateSecondIndexTime) / 1000} sec")



        def r = benchmark (measureCpuTime: true, verbose: true, warmUpTime: 5) {
            'paginateReportEntity' {
                100.times { t ->
                    reportService.paginateReportEntity("1411", 5, 20)
                }
            }
            'paginateReportEntityBySecondIndex' {
                100.times { t ->
                    reportService.paginateReportEntity("1411", 5, 20)
                }
            }
        }
        r.prettyPrint()
    }

}
