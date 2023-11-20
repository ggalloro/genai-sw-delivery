/*
 * Copyright 2020, Google LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package anthos.samples.bankofanthos.ledgerwriter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static anthos.samples.bankofanthos.ledgerwriter.ExceptionMessages.
        EXCEPTION_MESSAGE_INVALID_NUMBER;
import static anthos.samples.bankofanthos.ledgerwriter.ExceptionMessages.
        EXCEPTION_MESSAGE_NOT_AUTHENTICATED;
import static anthos.samples.bankofanthos.ledgerwriter.ExceptionMessages.
        EXCEPTION_MESSAGE_SEND_TO_SELF;
import static anthos.samples.bankofanthos.ledgerwriter.ExceptionMessages.
        EXCEPTION_MESSAGE_INVALID_AMOUNT;

/**
 * Validator to authenticate transaction.
 *
 * Functions to validate transaction details before adding to the ledger.
 */
@Component
public class TransactionValidator {

    // account ids should be 10 digits between 0 and 9 -- Fixed Now
    private static final Pattern ACCT_REGEX = Pattern.compile("^[0-9]{10}$");
    // route numbers should be 9 digits between 0 and 9
    private static final Pattern ROUTE_REGEX = Pattern.compile("^[0-9]{9}$");

    private static final Logger LOGGER =
        LogManager.getLogger(TransactionValidator.class);

    private void verifyAccountNumberFormat(String acctNum)
            throws PatternSyntaxException {
        if (!ACCT_REGEX.matcher(acctNum).matches()) {
            throw new PatternSyntaxException("Invalid transaction: Invalid account number", ACCT_REGEX.toString(), 0);
        }
    }

    private void verifyRoutingNumberFormat(String routeNum)
            throws IllegalArgumentException {
        if (!ROUTE_REGEX.matcher(routeNum).matches()) {
            LOGGER.error("Invalid transaction: Invalid routing number");
            throw new PatternSyntaxException("Invalid transaction: Invalid routing number", ROUTE_REGEX.toString(), 0);
        }
    }

    /**
     *   - Ensure sender is the same user authenticated by auth token
     *   - Ensure account and routing numbers are in the correct format
     *   - Ensure sender and receiver are different accounts
     *   - Ensure amount is positive
     *
     * @param authedAccount  the currently authenticated user account
     * @param transaction    the transaction object
     * @param bearerToken    the token used to authenticate request
     *
     * @throws IllegalArgumentException  on validation error
     */
    public void validateTransaction(String localRoutingNum, String authedAcct,
                                     Transaction transaction)
            throws IllegalArgumentException {
        LOGGER.debug("Validating transaction");
        final String fromAcct = transaction.getFromAccountNum();
        final String fromRoute = transaction.getFromRoutingNum();
        final String toAcct = transaction.getToAccountNum();
        final String toRoute = transaction.getToRoutingNum();
        final Integer amount = transaction.getAmount();
        
        try {
                // Validate account numbers
                verifyAccountNumberFormat(fromAcct);
                verifyAccountNumberFormat(toAcct);

                // Validate routing numbers
                verifyRoutingNumberFormat(fromRoute);
                verifyRoutingNumberFormat(toRoute);
        } catch (PatternSyntaxException e) {
                LOGGER.error(e.toString());
                throw new IllegalArgumentException(EXCEPTION_MESSAGE_INVALID_NUMBER);
        }
        
        // If this is an internal transaction,
        // ensure it originated from the authenticated user.
        if (fromRoute.equals(localRoutingNum) && !fromAcct.equals(authedAcct)) {
            LOGGER.error("Invalid transaction: Sender not authorized");
            throw new IllegalArgumentException(
                    EXCEPTION_MESSAGE_NOT_AUTHENTICATED);
        }
        // Ensure sender isn't receiver.
        if (fromAcct.equals(toAcct) && fromRoute.equals(toRoute)) {
            LOGGER.error("Invalid transaction: Sender is also receiver");
            throw new IllegalArgumentException(EXCEPTION_MESSAGE_SEND_TO_SELF);
        }
        // Ensure amount is valid value.
        if (amount <= 0) {
            LOGGER.error("Invalid transaction: Transaction amount " + amount + " is invalid");
            throw new IllegalArgumentException(
                    EXCEPTION_MESSAGE_INVALID_AMOUNT);
        }
    }
}
