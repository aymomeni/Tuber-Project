//
//  Registration2ViewController.swift
//  Tuber
//
//  Created by Hyunjin Cho on 2017. 1. 19..
//  Copyright © 2017년 Tuber. All rights reserved.
//
import UIKit

class Registration2ViewController: UIViewController,UIPickerViewDataSource,UIPickerViewDelegate
{
    //@IBOutlet weak var picker: UIPickerView!
    
    let pickerData = ["Master", "Visa"];
    var cardType = "";
    
    @IBOutlet weak var picker: UIPickerView!
    var passedInfo = [String()];
    @IBOutlet weak var cardNumber: UITextField!
    @IBOutlet weak var CVV: UITextField!
    @IBOutlet weak var month: UITextField!
    @IBOutlet weak var year: UITextField!
    @IBOutlet weak var doneButton: UIButton!
    
   // @IBOutlet weak var doneButton: UIButton!
    @available(iOS 2.0, *)
    func numberOfComponents(in pickerView: UIPickerView) -> Int
    {
        return 1;
    }
    
    
    override func viewDidLoad()
    {
        super.viewDidLoad()
        picker.dataSource = self;
        picker.delegate = self;
        print(passedInfo)
    }
    
    override func didReceiveMemoryWarning()
    {
        super.didReceiveMemoryWarning()
    }
    
    //MARK: Data Sources
    func numberOfComponentsInPickerView(in pickerView: UIPickerView) -> Int
    {
        return 1;
    }
    
    func pickerView(_ pickerView: UIPickerView, numberOfRowsInComponent component: Int) -> Int
    {
        return pickerData.count;
    }
    
    //MARK: Delegates
    func pickerView(_ pickerView: UIPickerView, titleForRow row: Int, forComponent component: Int) -> String?
    {
        return pickerData[row];
    }
    
    @IBAction func done(_ sender: Any) {
        let format = DateFormatter();
        format.dateFormat = "yyyy-MM-dd";
        
        let url = "http://tuber-test.cloudapp.net/ProductRESTService.svc/createuser";
        //creating the post parameter by concatenating the keys and values from text field
        let postParameters = "{\"userEmail\":\"" + passedInfo[1] +
            "\",\"userPassword\":\"" + passedInfo[4] +
            "\",\"userFirstName\":\"" + passedInfo[2] +
            "\",\"userLastName\":\"" + passedInfo[3] +
            "\",\"userBillingAddress\":\"" + "hardedcodedaddress" +
            "\",\"userBillingCity\":\"" + "Seoul" +
            "\",\"userBillingState\":\"" + "KR" +
            "\",\"userBillingCCNumber\":\"" + cardNumber.text! +
            "\",\"userBillingCCExpDate\":\"" + year.text!+"-"+month.text! + "-01" +
            "\",\"userBillingCCV\":\"" + CVV.text! + "\"}";
        
        let sr = ServerRequest();
        var responseCode:Int;
        responseCode = -1;
        var JSON:NSDictionary?;
        
        sr.runRequest(inputJSON: postParameters, server: url)
        {
            res,myJSON in
            JSON = myJSON;
            responseCode = res;
            if (responseCode == 200)
            {
                //parsing the json
                //parsing the json
                if let parseJSON = JSON {
                    
                OperationQueue.main.addOperation{
                    self.performSegue(withIdentifier: "regDone", sender: nil)
                    }
                }
            }
    }
        
}
}
