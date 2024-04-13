package edu.kalinin.acc.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import edu.kalinin.acc.data.Output;
import edu.kalinin.acc.dto.BaseDTO;
import edu.kalinin.acc.entity.Entry;
import edu.kalinin.acc.helper.ParseHelper;

@Mapper(componentModel = "spring",
        uses = {
                ParseHelper.class
        })
public interface EntryMapper {
    @Mapping(target = "id", ignore = true)

    @Mapping(target = "sourceOfPosting", expression = "java(header.getSystemId() + \".\" + header.getModuleId())")
    @Mapping(source = "header.operationBranchCode", target = "operationBranch")

    @Mapping(source = "data.debitAccount", target = "debitAccount")
    @Mapping(source = "data.creditAccount", target = "creditAccount")
    @Mapping(source = "data.debitCurrencyCode", target = "debitCurrencyCode")
    @Mapping(source = "data.creditCurrencyCode", target = "creditCurrencyCode")
    @Mapping(source = "data.debitValueDate", target = "debitValueDate")
    @Mapping(source = "data.creditValueDate", target = "creditValueDate")
    @Mapping(source = "data.debitAmount", target = "debitAmount")
    @Mapping(source = "data.creditAmount", target = "creditAmount")
    @Mapping(source = "data.debitLocalAmount", target = "debitLocalAmount")
    @Mapping(source = "data.creditLocalAmount", target = "creditLocalAmount")
    @Mapping(source = "data.narrative", target = "narrative")
    @Mapping(source = "data.reversalReference", target = "reversalReference")
    @Mapping(source = "data.multiOffsetReference", target = "multiOffsetReference")
    @Mapping(source = "data.postingDescription", target = "postingDescription")
    @Mapping(source = "data.documentId", target = "documentId")
    @Mapping(source = "data.channelName", target = "channelName")
    Entry toEntry(BaseDTO header, Output data);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "sourceOfPosting", expression = "java(header.getSystemId() + \".\" + header.getModuleId())")
    @Mapping(source = "header.operationBranchCode", target = "operationBranch")
    @Mapping(source = "data.debitAccount", target = "debitAccount")
    @Mapping(source = "data.creditAccount", target = "creditAccount")
    @Mapping(source = "data.debitCurrencyCode", target = "debitCurrencyCode")
    @Mapping(source = "data.creditCurrencyCode", target = "creditCurrencyCode")
    @Mapping(source = "data.debitValueDate", target = "debitValueDate")
    @Mapping(source = "data.creditValueDate", target = "creditValueDate")
    @Mapping(source = "data.debitAmount", target = "debitAmount")
    @Mapping(source = "data.creditAmount", target = "creditAmount")
    @Mapping(source = "data.debitLocalAmount", target = "debitLocalAmount")
    @Mapping(source = "data.creditLocalAmount", target = "creditLocalAmount")
    @Mapping(source = "data.narrative", target = "narrative")
    @Mapping(source = "data.reversalReference", target = "reversalReference")
    @Mapping(source = "data.multiOffsetReference", target = "multiOffsetReference")
    @Mapping(source = "data.postingDescription", target = "postingDescription")
    @Mapping(source = "data.documentId", target = "documentId")
    @Mapping(source = "data.channelName", target = "channelName")
    Entry toEntry(edu.kalinin.acc.dto.external.input.BaseDTO header, Output data);
}