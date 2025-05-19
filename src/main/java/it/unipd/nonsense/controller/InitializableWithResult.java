package it.unipd.nonsense.controller;

import it.unipd.nonsense.utils.AnalysisResult;

public interface InitializableWithResult {
    void initializeWithResult(AnalysisResult result);
}