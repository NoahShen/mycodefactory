package io.github.noahshen.secondindex.services

import io.github.noahshen.secondindex.entity.ReportEntity
/**
 * Created by noahshen on 15-1-8.
 */
class ReportService {

    void addReportEntity(ReportEntity reportEntity) {
        reportEntity.save()
    }

    List paginateReportEntity(String yymm, Integer page, Integer maxSize) {
        ReportEntity.find {
            eq("yymm", yymm)
            order("reportId", "asc")
            offset((page - 1) * maxSize)
            max(maxSize)
        }
    }

    List paginateReportEntityBySecondIndex(String yymm, Integer page, Integer maxSize) {
        // desc ???
        TreeMap countSubMap = monthCountIndex.subMap(monthCountIndex.firstKey(), yymm)
        Integer monthOffset = countSubMap.values().inject(0) {totalOffset, offset ->
            totalOffset += offset
        }

        TreeMap offsetSubMap = offsetCountIndex.subMap(offsetCountIndex.firstKey(), monthOffset)
        def lastEntry = offsetSubMap.lastEntry()
        Integer indexOffset = lastEntry.key
        Integer reportId = lastEntry.value

        Integer queryOffset = (monthOffset - indexOffset) + (page - 1) * maxSize
        ReportEntity.find {
            gte("reportId", reportId)
            order("reportId", "asc")
            offset(queryOffset)
            max(maxSize)
        }
    }

    TreeMap monthCountIndex = new TreeMap()

    void createMonthCountIndex(List allMonth) {
        monthCountIndex.clear()
        allMonth.each { oneMonth ->
            Integer count = ReportEntity.count {
                eq("yymm", oneMonth)
            }
            monthCountIndex.put(oneMonth, count)
        }
    }

    TreeMap offsetCountIndex = new TreeMap()

    void createOffsetCountIndex(List offsets) {
        offsetCountIndex.clear()
        offsets.each { oneOffset ->
            ReportEntity entity = ReportEntity.findFirst {
                order("reportId", "asc")
                offset(oneOffset)
            }
            if (entity) {
                offsetCountIndex.put(oneOffset, entity.reportId)
            }
        }
    }
}
