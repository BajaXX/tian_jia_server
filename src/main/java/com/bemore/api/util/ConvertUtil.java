package com.bemore.api.util;

import com.bemore.api.entity.Enterprise;
import com.bemore.api.entity.EnterpriseLog;
import com.bemore.api.entity.Member;
import com.bemore.api.entity.MemberLog;
import com.bemore.api.entity.Person;
import com.bemore.api.entity.PersonLog;

public class ConvertUtil {

	public static EnterpriseLog convert(Enterprise enterprise){
		EnterpriseLog log = new EnterpriseLog();
		
		log.setBelongIndustry(enterprise.getBelongIndustry());
		log.setBusiness(enterprise.getBusiness());
		log.setCapital(enterprise.getCapital());
		log.setCurrency(enterprise.getCurrency());
		log.setEndDate(enterprise.getEndDate());
		log.setEnterpriseId(enterprise.getId());
		log.setEnterpriseNo(enterprise.getEnterpriseNo());
		log.setEnterpriseWordNo(enterprise.getEnterpriseWordNo());
		log.setFollower(enterprise.getFollower());
		log.setGarden(enterprise.getGarden());
		log.setIndustry(enterprise.getIndustry());
		log.setIndustryCode(enterprise.getIndustryCode());
		log.setIntroducer(enterprise.getFollower());

		log.setInstitutionalType(enterprise.getInstitutionalType());
		log.setInvestmentType(enterprise.getInvestmentType());
		log.setFundManagementScale(enterprise.getFundManagementScale());
		log.setMoveType(enterprise.getMoveType());
		log.setDesignatedContact(enterprise.getDesignatedContact());
		log.setDesignatedContactPhone(enterprise.getDesignatedContactPhone());
		log.setBeIndependentInvestmentPromotion(enterprise.getBeIndependentInvestmentPromotion());
		log.setSettledDate(enterprise.getSettledDate());

		log.setMotherCompany(enterprise.getMotherCompany());
		log.setName(enterprise.getName());
		log.setNoLimit(enterprise.getNoLimit());
		log.setPaperName(enterprise.getPaperName());
		log.setPaperNo(enterprise.getPaperNo());
		log.setProcess(enterprise.getProcess());
		log.setRealCapital(enterprise.getRealCapital());
		log.setRegisterAddress(enterprise.getRegisterAddress());
		log.setRegisterNum(enterprise.getRegisterNum());
		log.setSource(enterprise.getSource());
		log.setStartDate(enterprise.getStartDate());
		log.setType(enterprise.getType());
		log.setZipcode(enterprise.getZipcode());
				
		return log;
	}
	
	public static Enterprise convert(EnterpriseLog enterprise){
		Enterprise log = new Enterprise();

		log.setBelongIndustry(enterprise.getBelongIndustry());
		log.setBusiness(enterprise.getBusiness());
		log.setCapital(enterprise.getCapital());
		log.setCurrency(enterprise.getCurrency());
		log.setEndDate(enterprise.getEndDate());
		log.setId(enterprise.getEnterpriseId());
		log.setEnterpriseNo(enterprise.getEnterpriseNo());
		log.setEnterpriseWordNo(enterprise.getEnterpriseWordNo());
		log.setFollower(enterprise.getFollower());
		log.setGarden(enterprise.getGarden());
		log.setIndustry(enterprise.getIndustry());
		log.setIndustryCode(enterprise.getIndustryCode());
		log.setIntroducer(enterprise.getFollower());
		log.setInstitutionalType(enterprise.getInstitutionalType());

		log.setMotherCompany(enterprise.getMotherCompany());
		log.setName(enterprise.getName());
		log.setNoLimit(enterprise.getNoLimit());
		log.setPaperName(enterprise.getPaperName());
		log.setPaperNo(enterprise.getPaperNo());
		log.setProcess(enterprise.getProcess());
		log.setRealCapital(enterprise.getRealCapital());
		log.setRegisterAddress(enterprise.getRegisterAddress());
		log.setRegisterNum(enterprise.getRegisterNum());
		log.setSource(enterprise.getSource());
		log.setStartDate(enterprise.getStartDate());
		log.setType(enterprise.getType());
		log.setZipcode(enterprise.getZipcode());
				
		return log;
	}
	
	public static Person convert(PersonLog person){
		Person log = new Person();
		
		log.setAddress(person.getAddress());
		log.setAuthority(person.getAuthority());
		log.setBack(person.getBack());
		log.setBirthday(person.getBirthday());
		log.setCountry(person.getCountry());
		log.setEmail(person.getEmail());
		log.setEndDate(person.getEndDate());
		log.setEnterpriseId(person.getEnterpriseId());
		log.setFront(person.getFront());
		log.setIdcard(person.getIdcard());
		log.setIsContact(person.getIsContact());
		log.setIsFinance(person.getIsFinance());
		log.setIsMaster(person.getIsMaster());
		log.setIsTax(person.getIsTax());
		log.setIsTicket(person.getIsTicket());
		log.setIsOldContact(person.getIsOldContact());
		log.setIsOldFinance(person.getIsOldFinance());
		log.setIsOldMaster(person.getIsOldMaster());
		log.setIsOldTax(person.getIsOldTax());
		log.setIsOldTicket(person.getIsOldTicket());
		log.setMemo(person.getMemo());
		log.setMobile(person.getMobile());
		log.setName(person.getName());
		log.setNation(person.getNation());
		log.setOfficeAddress(person.getOfficeAddress());
		log.setOfficeFax(person.getOfficeFax());
		log.setOfficePhone(person.getOfficePhone());
		log.setOfficeZipcode(person.getOfficeZipcode());
		log.setPhone(person.getPhone());
		log.setSex(person.getSex());
		log.setStartDate(person.getStartDate());
		log.setType(person.getType());
		log.setZipcode(person.getZipcode());
				
		return log;
	}
	
	public static PersonLog convert(Person person){
		PersonLog log = new PersonLog();
		
		log.setAddress(person.getAddress());
		log.setAuthority(person.getAuthority());
		log.setBack(person.getBack());
		log.setBirthday(person.getBirthday());
		log.setCountry(person.getCountry());
		log.setEmail(person.getEmail());
		log.setEndDate(person.getEndDate());
		log.setEnterpriseId(person.getEnterpriseId());
		log.setFront(person.getFront());
		log.setIdcard(person.getIdcard());
		log.setIsContact(person.getIsContact());
		log.setIsFinance(person.getIsFinance());
		log.setIsMaster(person.getIsMaster());
		log.setIsTax(person.getIsTax());
		log.setIsTicket(person.getIsTicket());
		log.setIsOldContact(person.getIsOldContact());
		log.setIsOldFinance(person.getIsOldFinance());
		log.setIsOldMaster(person.getIsOldMaster());
		log.setIsOldTax(person.getIsOldTax());
		log.setIsOldTicket(person.getIsOldTicket());
		log.setMemo(person.getMemo());
		log.setMobile(person.getMobile());
		log.setName(person.getName());
		log.setNation(person.getNation());
		log.setOfficeAddress(person.getOfficeAddress());
		log.setOfficeFax(person.getOfficeFax());
		log.setOfficePhone(person.getOfficePhone());
		log.setOfficeZipcode(person.getOfficeZipcode());
		log.setPhone(person.getPhone());
		log.setSex(person.getSex());
		log.setStartDate(person.getStartDate());
		log.setType(person.getType());
		log.setZipcode(person.getZipcode());
				
		return log;
	}
	
	public static Member convert(MemberLog member){
		Member log = new Member();
		
		log.setAddress(member.getAddress());
		log.setAuthority(member.getAuthority());
		log.setBack(member.getBack());
		log.setBirthday(member.getBirthday());
		log.setCountry(member.getCountry());
		log.setEmail(member.getEmail());
		log.setEndDate(member.getEndDate());
		log.setEnterpriseId(member.getEnterpriseId());
		log.setFront(member.getFront());
		log.setIdcard(member.getIdcard());		
		log.setIsDirector(member.getIsDirector());
		log.setIsStock(member.getIsStock());
		log.setIsSupervisor(member.getIsSupervisor());
		log.setIsOldDirector(member.getIsOldDirector());
		log.setIsOldStock(member.getIsOldStock());
		log.setIsOldSupervisor(member.getIsOldSupervisor());		
		log.setPutAmount(member.getPutAmount());
		log.setPutDate(member.getPutDate());
		log.setPutType(member.getPutType());
		log.setRealPutAmount(member.getRealPutAmount());
		log.setRealPutDate(member.getRealPutDate());	
		log.setOldPutAmount(member.getOldPutAmount());
		log.setOldPutDate(member.getOldPutDate());
		log.setOldPutType(member.getOldPutType());			
		log.setMemo(member.getMemo());
		log.setMobile(member.getMobile());
		log.setName(member.getName());
		log.setNation(member.getNation());
		log.setOfficeAddress(member.getOfficeAddress());
		log.setOfficeFax(member.getOfficeFax());
		log.setOfficePhone(member.getOfficePhone());
		log.setOfficeZipcode(member.getOfficeZipcode());
		log.setPhone(member.getPhone());
		log.setSex(member.getSex());
		log.setStartDate(member.getStartDate());
		log.setType(member.getType());
		log.setZipcode(member.getZipcode());
				
		return log;
	}
	
	public static MemberLog convert(Member member){
		MemberLog log = new MemberLog();
		
		log.setAddress(member.getAddress());
		log.setAuthority(member.getAuthority());
		log.setBack(member.getBack());
		log.setBirthday(member.getBirthday());
		log.setCountry(member.getCountry());
		log.setEmail(member.getEmail());
		log.setEndDate(member.getEndDate());
		log.setEnterpriseId(member.getEnterpriseId());
		log.setFront(member.getFront());
		log.setIdcard(member.getIdcard());		
		log.setIsDirector(member.getIsDirector());
		log.setIsStock(member.getIsStock());
		log.setIsSupervisor(member.getIsSupervisor());
		log.setIsOldDirector(member.getIsOldDirector());
		log.setIsOldStock(member.getIsOldStock());
		log.setIsOldSupervisor(member.getIsOldSupervisor());		
		log.setPutAmount(member.getPutAmount());
		log.setPutDate(member.getPutDate());
		log.setPutType(member.getPutType());
		log.setRealPutAmount(member.getRealPutAmount());
		log.setRealPutDate(member.getRealPutDate());	
		log.setOldPutAmount(member.getOldPutAmount());
		log.setOldPutDate(member.getOldPutDate());
		log.setOldPutType(member.getOldPutType());			
		log.setMemo(member.getMemo());
		log.setMobile(member.getMobile());
		log.setName(member.getName());
		log.setNation(member.getNation());
		log.setOfficeAddress(member.getOfficeAddress());
		log.setOfficeFax(member.getOfficeFax());
		log.setOfficePhone(member.getOfficePhone());
		log.setOfficeZipcode(member.getOfficeZipcode());
		log.setPhone(member.getPhone());
		log.setSex(member.getSex());
		log.setStartDate(member.getStartDate());
		log.setType(member.getType());
		log.setZipcode(member.getZipcode());
				
		return log;
	}
	
}
