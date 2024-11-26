package com.udemy.HelloBatch.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.udemy.HelloBatch.validator.HelloJobParameterValidator;

@Configuration
public class SpringConfig {
	
	private final JobLauncher jobLauncher;
	private final JobRepository jobRepository;
	private final PlatformTransactionManager transactionManager;
	
	@Autowired
	@Qualifier("HelloTask")//これでタスクを作成したコンポーネントの名称と一致したものを使います
	private Tasklet HelloTask;
	
	@Autowired
	@Qualifier("HelloTask2")//これでタスクを作成したコンポーネントの名称と一致したものを使います
	private Tasklet HelloTask2;
	
	@Autowired
	private ItemReader<String> helloReader;
	
	@Autowired
	private ItemProcessor<String, String> helloProcessor;
	
	@Autowired
	private ItemWriter<String> helloWritter;
	
	@Autowired
	private JobExecutionListener helloJobExecutionListener;
	
	public SpringConfig(JobLauncher jobLauncher, JobRepository jobRepository,
			PlatformTransactionManager transactionManager) {
		this.jobLauncher = jobLauncher;
		this.jobRepository = jobRepository;
		this.transactionManager = transactionManager;
	}
	
	//バリデーションを実装
	@Bean
	public JobParametersValidator jobParametersValidator() {
		return new HelloJobParameterValidator();
	}
	
	//StepBuilderに対して、作成したタスク処理を指定してあげる
	@Bean
	public Step HelloTaskLetStep1() {
		return new StepBuilder("HelloTaskLetStep1", jobRepository)
				.tasklet(HelloTask, transactionManager)
				.build();
	}
	
	@Bean
	public Step HelloTaskLetStep2() {
		return new StepBuilder("HelloTaskLetStep2", jobRepository)
				.tasklet(HelloTask2, transactionManager)
				.build();
	}
	
	@Bean
	public Step HelloChunkStep() {
		return new StepBuilder("HelloChunkStep", jobRepository)
				.<String, String>chunk(1, transactionManager)
				.reader(helloReader)
				.processor(helloProcessor)
				.writer(helloWritter)
				.build();
				
	}
	//ここでStepを呼び出すよ
	@Bean
	public Job HelloJob() {
		return new JobBuilder("HelloJob", jobRepository)
				.incrementer(new RunIdIncrementer())//採番を付けて、バッチ処理の整合性を保つ
				.start(HelloTaskLetStep1())//Stepで作成したファンクションを呼び出す
				.next(HelloTaskLetStep2())
				.next(HelloChunkStep())
				.validator(jobParametersValidator())//バリデーションを呼び出す
				.listener(helloJobExecutionListener)
				.build();
	}
	
}
