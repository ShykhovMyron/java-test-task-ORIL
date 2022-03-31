package test.task.it;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import test.task.config.AppProperties;
import test.task.config.AppTestProperties;
import test.task.entity.CryptoDataPrice;
import test.task.exeption.NonexistentCurrencyName;
import test.task.repository.CryptocurrencyInfoRepository;
import test.task.service.CryptocurrencyService;
import test.task.testUtils.DefaultTestUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@TestPropertySource("/application-test.yaml")
public class CryptocurrencyServiceIT {
    @Autowired
    private CryptocurrencyService cryptocurrencyService;

    @MockBean
    private AppProperties appProperties;
    @MockBean
    private CryptocurrencyInfoRepository cryptoInfoRepo;

    @Test
    void getMinCryptoPricePositiveTest() throws NonexistentCurrencyName {
        CryptoDataPrice testCryptoDataPrice = DefaultTestUtils.getTestCryptoDataPrice();
        String baseCurrency = DefaultTestUtils.getBaseCurrencyFromConfig();

        Mockito.doReturn(AppTestProperties.currencyPairs).when(appProperties).getCurrencyPairs();
        Mockito.doReturn(testCryptoDataPrice)
                .when(cryptoInfoRepo).findTopByBaseCurrencyOrderByPriceAsc(Mockito.anyString());

        Assertions.assertEquals(
                new BigDecimal(testCryptoDataPrice.getPrice()).toString()
                , cryptocurrencyService.getMinCryptoPrice(baseCurrency)
        );
    }

    @Test
    void getMinCryptoPriceExceptionTest() {
        Mockito.doReturn(AppTestProperties.currencyPairs).when(appProperties).getCurrencyPairs();
        Mockito.doReturn(DefaultTestUtils.getTestCryptoDataPrice())
                .when(cryptoInfoRepo).findTopByBaseCurrencyOrderByPriceAsc(Mockito.anyString());

        Assertions.assertThrows(
                NonexistentCurrencyName.class
                , () -> cryptocurrencyService.getMinCryptoPrice("")
        );
    }

    @Test
    void getMaxCryptoPricePositiveTest() throws NonexistentCurrencyName {
        CryptoDataPrice testCryptoDataPrice = DefaultTestUtils.getTestCryptoDataPrice();
        String baseCurrency = DefaultTestUtils.getBaseCurrencyFromConfig();

        Mockito.doReturn(AppTestProperties.currencyPairs).when(appProperties).getCurrencyPairs();
        Mockito.doReturn(testCryptoDataPrice)
                .when(cryptoInfoRepo).findTopByBaseCurrencyOrderByPriceDesc(Mockito.anyString());

        Assertions.assertEquals(
                new BigDecimal(testCryptoDataPrice.getPrice()).toString()
                , cryptocurrencyService.getMaxCryptoPrice(baseCurrency)
        );
    }

    @Test
    void getMaxCryptoPriceExceptionTest() {
        Mockito.doReturn(AppTestProperties.currencyPairs).when(appProperties).getCurrencyPairs();
        Mockito.doReturn(DefaultTestUtils.getTestCryptoDataPrice())
                .when(cryptoInfoRepo).findTopByBaseCurrencyOrderByPriceDesc(Mockito.anyString());

        Assertions.assertThrows(
                NonexistentCurrencyName.class
                , () -> cryptocurrencyService.getMaxCryptoPrice("")
        );
    }

    @Test
    void getWithPaginationPositiveTest() throws NonexistentCurrencyName {
        List<CryptoDataPrice> expected = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            expected.add(DefaultTestUtils.getTestCryptoDataPrice());
        }

        Mockito.doReturn(AppTestProperties.currencyPairs).when(appProperties).getCurrencyPairs();
        Mockito.doReturn(expected).when(cryptoInfoRepo)
                .findAllByBaseCurrencyOrderByPriceAsc(Mockito.anyString(), Mockito.any());

        Assertions.assertEquals(
                expected
                , cryptocurrencyService.getWithPagination(DefaultTestUtils.getBaseCurrencyFromConfig(), 0, 5)
        );
    }

    @Test
    void getWithPaginationExceptionTest() {
        Mockito.doReturn(AppTestProperties.currencyPairs).when(appProperties).getCurrencyPairs();
        Mockito.doReturn(new ArrayList<>()).when(cryptoInfoRepo)
                .findAllByBaseCurrencyOrderByPriceAsc(Mockito.anyString(), Mockito.any());

        Assertions.assertThrows(
                NonexistentCurrencyName.class
                , () -> cryptocurrencyService.getWithPagination("", 0, 5)
        );
    }
}