package com.unimag.ahorroplusia.mapper;

import com.unimag.ahorroplusia.dto.ReportDTO;
import com.unimag.ahorroplusia.entity.entities.Report;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReportMapper {

    ReportDTO reportDTOToReportDTO(Report report);
    Report reportDTOToReport(ReportDTO reportDTO);

}
