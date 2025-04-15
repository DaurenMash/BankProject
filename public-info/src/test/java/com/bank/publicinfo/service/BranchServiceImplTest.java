package com.bank.publicinfo.service;

import com.bank.publicinfo.dto.BranchDto;
import com.bank.publicinfo.entity.Branch;
import com.bank.publicinfo.exception.GlobalExceptionHandler;
import com.bank.publicinfo.mapper.BranchMapper;
import com.bank.publicinfo.repository.AtmRepository;
import com.bank.publicinfo.repository.BranchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import com.bank.publicinfo.exception.DataAccessException;
import com.bank.publicinfo.exception.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalTime;

@ExtendWith(MockitoExtension.class)
public class BranchServiceImplTest {

    @Mock
    private BranchRepository branchRepository;

    @Mock
    private BranchMapper branchMapper;

    @Mock
    private AtmRepository atmRepository;

    @Mock
    private GlobalExceptionHandler globalExceptionHandler;

    @InjectMocks
    private BranchServiceImpl branchService;

    @Value("${spring.kafka.topics.error-log.name}")
    private String errorTopic;

    private Branch branch;
    private BranchDto branchDto;

    @BeforeEach
    void setUp() {
        branch = Branch.builder()
                .id(1L)
                .address("123 Main St")
                .phoneNumber(1234567890L)
                .city("New York")
                .startOfWork(LocalTime.of(9, 0))
                .endOfWork(LocalTime.of(18, 0))
                .atms(new HashSet<>())
                .build();

        branchDto = BranchDto.builder()
                .id(1L)
                .address("123 Main St")
                .phoneNumber(1234567890L)
                .city("New York")
                .startOfWork(LocalTime.of(9, 0))
                .endOfWork(LocalTime.of(18, 0))
                .build();
    }

    @Test
    void testCreateNewBranch_Success() {
        when(branchMapper.toEntity(branchDto)).thenReturn(branch);
        when(branchMapper.toDto(branch)).thenReturn(branchDto);
        when(branchRepository.save(branch)).thenReturn(branch);

        BranchDto result = branchService.createNewBranch(branchDto);

        assertEquals(branchDto, result);
        verify(branchRepository, times(1)).save(branch);
    }

    @Test
    void testCreateNewBranch_NullInput() {
        assertThrows(IllegalArgumentException.class, () -> {
            branchService.createNewBranch(null);
        });

        verify(globalExceptionHandler, times(1))
                .handleException(any(IllegalArgumentException.class), eq(errorTopic));
    }

    @Test
    void testCreateNewBranch_UnexpectedException() {
        when(branchMapper.toEntity(branchDto)).thenThrow(new IllegalArgumentException("Invalid argument"));

        assertThrows(IllegalArgumentException.class, () -> {
            branchService.createNewBranch(branchDto);
        });

        verify(globalExceptionHandler, times(1))
                .handleException(any(IllegalArgumentException.class), eq(errorTopic));
    }


    @Test
    void testCreateNewBranch_Exception() {
        when(branchMapper.toEntity(branchDto)).thenReturn(branch);
        when(branchRepository.save(branch)).thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class, () -> {
            branchService.createNewBranch(branchDto);
        });

        verify(globalExceptionHandler, times(1))
                .handleException(any(RuntimeException.class), eq(errorTopic));
    }

    @Test
    void testUpdateBranch_Success() {
        when(branchRepository.findById(1L)).thenReturn(Optional.of(branch));
        when(branchMapper.toDto(branch)).thenReturn(branchDto);
        when(branchRepository.save(branch)).thenReturn(branch);

        BranchDto result = branchService.updateBranch(1L, branchDto);

        assertEquals(branchDto, result);
        verify(branchRepository, times(1)).save(branch);
    }

    @Test
    void testUpdateBranch_UnexpectedException() {
        when(branchRepository.findById(1L)).thenReturn(Optional.of(branch));
        doThrow(new IllegalArgumentException("Invalid argument")).when(branchMapper).updateFromDto(branchDto, branch);

        assertThrows(IllegalArgumentException.class, () -> {
            branchService.updateBranch(1L, branchDto);
        });

        verify(globalExceptionHandler, times(1))
                .handleException(any(IllegalArgumentException.class), eq(errorTopic));
    }


    @Test
    void testUpdateBranch_NullId() {
        assertThrows(IllegalArgumentException.class, () -> {
            branchService.updateBranch(null, branchDto);
        });

        verify(globalExceptionHandler, times(1))
                .handleException(any(IllegalArgumentException.class), eq(errorTopic));
    }

    @Test
    void testUpdateBranch_NullDto() {
        assertThrows(IllegalArgumentException.class, () -> {
            branchService.updateBranch(1L, null);
        });

        verify(globalExceptionHandler, times(1))
                .handleException(any(IllegalArgumentException.class), eq(errorTopic));
    }

    @Test
    void testUpdateBranch_NotFound() {
        when(branchRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            branchService.updateBranch(1L, branchDto);
        });

        verify(globalExceptionHandler, times(2))
                .handleException(any(EntityNotFoundException.class), eq(errorTopic));
    }

    @Test
    void testDeleteBranchById_Success() {
        when(branchRepository.findById(1L)).thenReturn(Optional.of(branch));
        doNothing().when(atmRepository).deleteAll(branch.getAtms());
        doNothing().when(branchRepository).delete(branch);

        branchService.deleteBranchById(1L);

        verify(atmRepository, times(1)).deleteAll(branch.getAtms());
        verify(branchRepository, times(1)).delete(branch);
    }

    @Test
    void testDeleteBranchById_NullId() {
        assertThrows(IllegalArgumentException.class, () -> {
            branchService.deleteBranchById(null);
        });

        verify(globalExceptionHandler, times(1))
                .handleException(any(IllegalArgumentException.class), eq(errorTopic));
    }

    @Test
    void testDeleteBranchById_NotFound() {
        when(branchRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            branchService.deleteBranchById(1L);
        });

        verify(globalExceptionHandler, times(2))
                .handleException(any(EntityNotFoundException.class), eq(errorTopic));
    }

    @Test
    void testDeleteBranchById_UnexpectedException() {
        when(branchRepository.findById(1L)).thenReturn(Optional.of(branch));
        doThrow(new RuntimeException("Database error")).when(atmRepository).deleteAll(branch.getAtms());

        assertThrows(RuntimeException.class, () -> {
            branchService.deleteBranchById(1L);
        });

        verify(globalExceptionHandler, times(1))
                .handleException(any(RuntimeException.class), eq(errorTopic));
    }


    @Test
    void testGetAllBranches_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Branch> branches = List.of(branch);
        Page<Branch> page = new PageImpl<>(branches, pageable, 1);

        when(branchRepository.findAll(pageable)).thenReturn(page);
        when(branchMapper.toDto(branch)).thenReturn(branchDto);

        List<BranchDto> result = branchService.getAllBranches(pageable);

        assertEquals(1, result.size());
        assertEquals(branchDto, result.get(0));
        verify(branchRepository, times(1)).findAll(pageable);
    }

    @Test
    void testGetAllBranches_NullPageable() {
        assertThrows(IllegalArgumentException.class, () -> {
            branchService.getAllBranches(null);
        });

        verify(globalExceptionHandler, times(1))
                .handleException(any(IllegalArgumentException.class), eq(errorTopic));
    }

    @Test
    void testGetAllBranches_UnexpectedException() {
        Pageable pageable = PageRequest.of(0, 10);
        when(branchRepository.findAll(pageable)).thenThrow(new IllegalArgumentException("Invalid argument"));

        assertThrows(IllegalArgumentException.class, () -> {
            branchService.getAllBranches(pageable);
        });

        verify(globalExceptionHandler, times(1))
                .handleException(any(IllegalArgumentException.class), eq(errorTopic));
    }


    @Test
    void testGetAllBranches_DataAccessException() {
        Pageable pageable = PageRequest.of(0, 10);
        when(branchRepository.findAll(pageable)).thenThrow(new DataAccessException("Database error") {});

        assertThrows(DataAccessException.class, () -> {
            branchService.getAllBranches(pageable);
        });

        verify(globalExceptionHandler, times(1))
                .handleException(any(DataAccessException.class), eq(errorTopic));
    }

    @Test
    void testGetBranchById_Success() {
        when(branchRepository.findById(1L)).thenReturn(Optional.of(branch));
        when(branchMapper.toDto(branch)).thenReturn(branchDto);

        BranchDto result = branchService.getBranchById(1L);

        assertEquals(branchDto, result);
        verify(branchRepository, times(1)).findById(1L);
    }

    @Test
    void testGetBranchById_NullId() {
        assertThrows(IllegalArgumentException.class, () -> {
            branchService.getBranchById(null);
        });

        verify(globalExceptionHandler, times(1))
                .handleException(any(IllegalArgumentException.class), eq(errorTopic));
    }

    @Test
    void testGetBranchById_UnexpectedException() {
        when(branchRepository.findById(1L)).thenThrow(new IllegalArgumentException("Invalid argument"));

        assertThrows(IllegalArgumentException.class, () -> {
            branchService.getBranchById(1L);
        });

        verify(globalExceptionHandler, times(1))
                .handleException(any(IllegalArgumentException.class), eq(errorTopic));
    }


    @Test
    void testGetBranchById_NotFound() {
        when(branchRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            branchService.getBranchById(1L);
        });

        verify(globalExceptionHandler, times(2))
                .handleException(any(EntityNotFoundException.class), eq(errorTopic));
    }
}
