package InterviewCoding;

public class GeminiSolutions {

    @Service
    Public class PaymentService{

        @Transactional
        Public void transferUsdtoInr( Long fromAccountId, Long ToAccountId, Float usdAmount, Float exchangeRate){

//if — null pointer expiation handling —

            Float amountINR = usdAmount.muliply(excghangeRate);

            Account valuefrom = Accountrepo.findByIDforupdate(fromAccountID);

            Account valueto  = AccountRepo.findByIdforUpdate(toAccountID);

            If (valuefrom.getBalance().compareTo(usdAMount) < 0){
                Throw new expection();
            }

            Valuefrom.setBalance(valuefrom.getBalance()-usdAmount);
            Valueto.setBalance(valueto.getBalance()+inrAmount);

            accountRepo.save(ValueFrom);
            accountRepo.save(ValueTo);

        }
    }
}
