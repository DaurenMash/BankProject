/*
package com.bank.account.controller;

import com.bank.account.dto.AccountDto;
import com.bank.account.entity.Account;
import com.bank.account.mapper.AccountMapper;
import com.bank.account.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "Accounts", description = "API for managing bank accounts")
public class AccountsController {
    private final AccountService accountService;
    private final AccountMapper accountMapper;

    @Autowired
    public AccountsController(AccountService accountService, AccountMapper accountMapper) {
        this.accountService = accountService;
        this.accountMapper = accountMapper;
    }


    @Operation(summary = "Get all accounts", description = "Returns list of all bank accounts")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping(value = "/accounts", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<AccountDto>> getAccounts() {
        List<Account> accounts = accountService.getAllAccounts();
        List<AccountDto> accountsDto = accounts.stream()
                .map(accountMapper::setDataFromEntityToDto)
                .toList();
        return ResponseEntity.status(HttpStatus.OK).body(accountsDto);
    }

    @Operation(summary = "Get account by ID", description = "Returns a account by its ID")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved account"),
            @ApiResponse(responseCode = "404", description = "Account not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping(value = "/accounts/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AccountDto> getAccount(@PathVariable Long id) {
        Account account = accountService.getAccountById(id);
        AccountDto accountDto = accountMapper.setDataFromEntityToDto(account);
        return ResponseEntity.status(HttpStatus.OK).body(accountDto);
    }

    @Operation(summary = "Create new account", description = "Creates a new bank account")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "Account successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping(value = "/save")
    public ResponseEntity<Void> saveAccount(@Valid @RequestBody AccountDto accountDto) {
        try {
            Account account = accountMapper.setDataToEntity(accountDto);
            accountService.createNewAccount(account);

            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Update existing account", description = "Updates an existing bank account")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "Account successfully created"),
            @ApiResponse(responseCode = "404", description = "Account not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping(value = "/update_account/{id}")
    public ResponseEntity<Void> updateAccount(@PathVariable Long id, @Valid @RequestBody AccountDto accountDto) {
        try {
            Account account = accountMapper.setDataToEntity(accountDto);
            accountService.updateCurrentAccount(id, account);

            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Delete existing account", description = "Removes an existing bank account")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "Account successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Account not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping(value = "/delete/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
        try {
            Account accountForDelete = accountService.getAccountById(id);
            if (accountForDelete == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404
            }
            accountService.deleteAccount(accountForDelete);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
*/
