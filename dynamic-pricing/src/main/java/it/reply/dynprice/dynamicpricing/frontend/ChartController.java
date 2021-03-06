package it.reply.dynprice.dynamicpricing.frontend;



import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import it.reply.dynprice.dynamicpricing.controller.PriceCalcController;
import it.reply.dynprice.dynamicpricing.easynotes.repository.PriceEntityCompetitorDao;
import it.reply.dynprice.dynamicpricing.persistence.dao.PriceDao;
import it.reply.dynprice.dynamicpricing.persistence.model.PriceEntity;
import it.reply.dynprice.dynamicpricing.persistence.model.PriceEntityCompetitor;
import it.reply.dynprice.dynamicpricing.persistence.model.PriceVariables;







@RestController
public class ChartController {
	
	private static final Logger logger = LoggerFactory.getLogger(ChartController.class);
	
	@Autowired
	private PriceDao priceDao;
	
	@Autowired
	private PriceEntityCompetitorDao priceEntityCompetitorDao;
	
	@Autowired
	PriceCalcController priceCalcController;
	
	@RequestMapping(value = "/chart/data", method = RequestMethod.GET, produces = "application/json")
	public String test() {
		
		List<PriceEntity> priceDataList = this.priceDao.findAll();
		DateFormat df = new SimpleDateFormat("MMM dd HH:mm");
		
		ChartData dummyChartData = new ChartData();
		
		priceDataList.sort(new PriceDataComparator());
		
		List<Double> dataList = new ArrayList<Double>();
		List<String> labelsList = new ArrayList<String>();
		
		List<PriceEntityCompetitor> competitorPriceDatalist = priceEntityCompetitorDao.findAll();
		
		List<Double> amazon = new ArrayList<Double>();
		List<Double> alibaba = new ArrayList<Double>();
		List<Double> adjusted = new ArrayList<Double>();
		
		for(PriceEntity priceEntity : priceDataList) {
			
			dataList.add(priceEntity.getPrice());
			labelsList.add(df.format(priceEntity.getUpdated()));
			
			dummyChartData.setTotal_revenue(null==priceEntity.getTotal_revenue()?0.0:priceEntity.getTotal_revenue());
			dummyChartData.setProfit_per_unit(null==priceEntity.getProfit_per_unit()?0.0:priceEntity.getProfit_per_unit());
			dummyChartData.setFraction_of_profit(null==priceEntity.getFraction_of_profit()?0.0:priceEntity.getFraction_of_profit());
		}
		
		for(PriceEntityCompetitor pec : competitorPriceDatalist) {
			
			if(pec.getName().equals("Amazon")) {
				amazon.add(pec.getPrice());
			}
			
			if(pec.getName().equals("Alibaba")) {
				alibaba.add(pec.getPrice());
			}
			
			if(pec.getName().equals("Adjusted")) {
				adjusted.add(pec.getPrice());
			}
			
			
		}
		
		String[] labelsArr = new String[labelsList.size()];
		labelsArr = labelsList.toArray(labelsArr);
		
		Double[] dataArr = new Double[dataList.size()];
		dataArr = dataList.toArray(dataArr);
		
		Double[] amazonArr = new Double[amazon.size()];
		amazonArr = amazon.toArray(amazonArr);
		
		Double[]alibabaArr = new Double[alibaba.size()];
		alibabaArr = alibaba.toArray(alibabaArr);
		
		Double[] adjustedArr = new Double[adjusted.size()];
		adjustedArr = adjusted.toArray(adjustedArr);
		
		dummyChartData.setData(dataArr);
		dummyChartData.setLabels(labelsArr);
		dummyChartData.setAlibaba(alibabaArr);
		dummyChartData.setAmazon(amazonArr);
		dummyChartData.setAdjusted(adjustedArr);
		
		
		Gson gson = new Gson();
		
		return gson.toJson(dummyChartData);
		
	}
	
	
	@RequestMapping(value = "/chart/update", method = RequestMethod.POST, produces = "application/json")
	public void update(@RequestBody String body) {
		
		logger.info(body);
		
		Gson gson = new Gson(); 
	
		
		
		AlgorithmInputData inputData = gson.fromJson(body, AlgorithmInputData.class);
		
		PriceVariables priceVariables = new PriceVariables();
		
		priceVariables.setMargin(inputData.getImargin());
		priceVariables.setCosts_dir_unit(inputData.getIcostu());
		priceVariables.setQnt_prod(inputData.getIastock());
		priceVariables.setCosts_dir(inputData.getIincos());
		priceVariables.setCosts_op(inputData.getIopcos());
		
		this.priceCalcController.calc_main(priceVariables);
		
		//ChartData dummyChartData = new ChartData();
		
		//Gson gson = new Gson();
		
		//return gson.toJson(dummyChartData);
		
	}

}
