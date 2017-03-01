//
//  Registration2ViewController.swift
//  Tuber
//
//  Created by Hyunjin Cho on 2017. 1. 19..
//  Copyright © 2017년 Tuber. All rights reserved.
//
import UIKit

class Registration2ViewController: UIViewController,UIPickerViewDataSource,UIPickerViewDelegate {
    
    @IBOutlet weak var picker: UIPickerView!;
    let pickerData = ["Master", "Visa"];
    var cardType = "";
    let server = "http://tuber-test.cloudapp.net/ProductRESTService.svc/createuser";
    
    var passedInfo = [String()]
    
    @IBOutlet weak var doneButton: UIButton!
    @IBOutlet weak var cardNumber: UITextField!
    @IBOutlet weak var CVV: UITextField!
    @IBOutlet weak var month: UITextField!
    @IBOutlet weak var year: UITextField!
    
    @available(iOS 2.0, *)
    func numberOfComponents(in pickerView: UIPickerView) -> Int {
        return 1;
    }
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        picker.dataSource = self;
        picker.delegate = self;
        
        print(passedInfo)
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    //MARK: Data Sources
    func numberOfComponentsInPickerView(in pickerView: UIPickerView) -> Int {
        return 1;
    }
    func pickerView(_ pickerView: UIPickerView, numberOfRowsInComponent component: Int) -> Int {
        return pickerData.count;
    }
    
    //MARK: Delegates
    func pickerView(_ pickerView: UIPickerView, titleForRow row: Int, forComponent component: Int) -> String? {
        cardType = pickerData[row];
        return pickerData[row];
    }
    func pickerView(_ pickerView: UIPickerView, didSelectRow row: Int, inComponent component: Int) {
    }
    
    @IBAction func doneButtonPress(_ sender: Any) {
        
        let format = DateFormatter();
        format.dateFormat = "yyyy-MM-dd";
        
        //created NSURL
        let requestURL = NSURL(string: server)
        
        //creating NSMutableURLRequest
        let request = NSMutableURLRequest(url: requestURL! as URL)
        
        //setting the method to post
        request.httpMethod = "POST"
        
        
        //creating the post parameter by concatenating the keys and values from text field
        let postParameters = "{\"userEmail\":\"" + passedInfo[1] +
            "\",\"userPassword\":\"" + passedInfo[4] +
            "\",\"userFirstName\":\"" + passedInfo[2] +
            "\",\"userLastName\":\"" + passedInfo[3] +
            "\",\"userBillingAddress\":\"" + "hardedcodedaddress" +
            "\",\"userBillingCity\":\"" + "Seoul" +
            "\",\"userBillingState\":\"" + "KR" +
            "\",\"userBillingCCNumber\":\"" + cardNumber.text! +
            "\",\"userBillingCCExpDate\":\"" + month.text!+"/"+year.text! +
            "\",\"userBillingCCV\":\"" + CVV.text! + "\"}"
        
        //adding the parameters to request body
        request.httpBody = postParameters.data(using: String.Encoding.utf8)
        request.addValue("application/json", forHTTPHeaderField: "Content-Type")
        request.addValue("application/json", forHTTPHeaderField: "Accept")
        
        
        //creating a task to send the post request
        let task = URLSession.shared.dataTask(with: request as URLRequest){
            data, response, error in
            
            if error != nil{
                print("error is \(error)")
                return;
            }
            
            let r = response as? HTTPURLResponse
            
            //parsing the response
            
            if (r?.statusCode == 200)
            {
                do {
                    //converting resonse to NSDictionary
                    let myJSON =  try JSONSerialization.jsonObject(with: data!, options: .allowFragments) as? NSDictionary
                    
                    
                    //parsing the json
                    if let parseJSON = myJSON {
                        
                        let defaults = UserDefaults.standard
                        
                        defaults.set(parseJSON["userEmail"] as! String?, forKey: "userEmail")
                        defaults.set(parseJSON["userStudentCourses"] as! Array<String>?, forKey: "userStudentCourses")
                        defaults.set(parseJSON["userToken"] as! String?, forKey: "userToken")
                        defaults.set(parseJSON["userTutorCourses"] as! Array<String>?, forKey: "userTutorCourses")
                        defaults.synchronize()
                        
                        print("Added to defaults")
                        
                        print(defaults.object(forKey: "userToken")!)
                        
                        OperationQueue.main.addOperation{
                            self.performSegue(withIdentifier: "registrationSuccess", sender: nil)
                        }
                    }
                }
                catch {
                    print(error)
                }
            }
            //rest of responses
            
        }
        //executing the task
        task.resume()
        
        
    }
    
}
