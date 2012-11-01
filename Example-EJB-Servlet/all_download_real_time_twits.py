import json
import datetime,time
from datetime import datetime as dt
from datetime import date
import urllib,urllib2,psycopg2
import tweepy
from xml.dom.minidom import parse, parseString
import random

#author: Wei Chen
#last modified 2011/9/20

def download_twitts():   
    def process(result):
        if result['geo'] is None:
            result.update({'geo':{}})
            result['geo'].update({'coordinates':None,'type':None})
        if not result.has_key('location'):
            result.update({'location':None})
        if not result.has_key('iso_language_code'):
            result.update({'iso_language_code':None})
        if result.has_key('place'):
            if result['place'] is None:
                result['place'].update({'full_name':None,'id':None,'type':None})
        else:
            result.update({'place':{'full_name':None,'id':None,'type':None}})
        return result
    
    #print "API status:",api.rate_limit_status(),'\n'
    f=open('fishnet_label.csv','r')
    f.readline()
    coords=[]
    for line in f.readlines():
        id,lat,lon=line.split(',')
        lon=lon.strip()
        coords.append([id,lat,lon,'30km'])

    """track={'china':'china',
           'obama':'obama OR barackobama',
           'libya':'libya',
           'japan':'japan',
           'gop':'gop',
           'financialcrisis':'financial crisis OR economic crisis OR debt crisis',
           'globalwarming':'global warming OR greenhouse effect OR climate warming'}"""
    track={'geog':'geography OR geographic OR geographical OR spatial'}
    conn = psycopg2.connect("dbname=us user=postgres")
    cur = conn.cursor()
    #c1,c2 are counters
    c1=0
    curSkipLst=[]
    preSkipLst=[]
    globeSkipSet=()
    while(1):
        #every loop willl load 100 new entries into the db based on since_id and until date
        for key, value in track.items():
            curSkipLst=[]
            i=-1
            t1=time.clock()
            while(i<len(coords)):
                i+=1
                if i in globeSkipSet:
                    continue
                
                if i==len(coords):
                    t2=time.clock()
                    print "Loop through ",len(coords)-len(globeSkipSet),', skipped',len(globeSkipSet),'. Total execution time:',(t2-t1)/60,' min'
                    preSkipLst.extend(curSkipLst)
                    globeSkipSet=set(preSkipLst)
                    print "globeSkipSet:",globeSkipSet
                    preSkipLst=curSkipLst
                    f=open('globeSkipSet_geog.txt','w')
                    f.write(','.join(map(str, preSkipLst))+'\n')
                    f.write(','.join(map(str, list(globeSkipSet))))
                    f.close()
                    break
                coord=coords[i]
                print coord[0]
                if int(coord[0])<67: continue
                cur.execute("SELECT max(id) FROM "+key+" WHERE gid="+str(coord[0]))
                sqlResults=cur.fetchall()
                since_id=sqlResults[0][0]
                if type(since_id) == 'Nonetype':
                    since_id=0
                print coord[0],',',','.join(coord[1:])
                url = "http://search.twitter.com/search.json?"+urllib.urlencode({'q':value,'lang':'en','rpp':100,'result_type':'mixed',
                'since_id':since_id,'geocode':','.join(coord[1:])})
                #print url,'\n'
                #exit()
                #while(not api.rate_limit_status()['remaining_hits']):
                #    print api.rate_limit_status()
                #    wait_time=api.rate_limit_status()['reset_time_in_seconds']-mktime(datetime.now().timetuple())
                #    print 'sleeping for %s minutes...' % (wait_time/60)
                #    time.sleep(wait_time)
                try:
                    f = urllib2.urlopen(url)
                except urllib2.URLError, e:
                    curSkipLst.append(i)
                    print url
                    print "URL error sleep 1 second\n"
                    time.sleep(1)
                    
                    def send_message(fromaddr, toaddrs, msg):
                        import smtplib                    
                        # Credentials (if needed)
                        username = 'dylovecw'
                        password = 'ohiostate'
                        
                        # The actual mail send
                        try:
                            server = smtplib.SMTP('smtp.gmail.com:587')
                        except smtplib.socket.gaierror:
                            print 'Gmail connect_err'
                        try:
                            server.ehlo()
                            server.starttls()
                            server.ehlo()
                            server.login(username,password)
                        except smtplib.SMTPAuthenticationError:
                            server.quit()
                            print 'Gmail login_err'      
                        try:
                            server.sendmail(fromaddr, toaddrs, msg)
                            print 'Message sending successful'
                        except Exception:
                            print 'Gmail send_err'
                        finally:
                            server.quit()
                            
                    """fromaddr = 'dylovecw@gmail.com'
                    now = dt.today().strftime("%d/%m/%y")
                    msg = ("From:%s\r\nTo: chen.1381@gmail.com\r\nSubject: Twitter Python Program Error\r\n\r\n Hello,\
                           \r\nUrl open error sleep 1 second. Thank you." % (fromaddr))
                    print now+'\n'
                    print msg+'\n'
                    toaddr  = 'chen.1381@gmail.com'"""
                    #send_message(fromaddr,toaddr,msg)
                    curSkipLst.append(i)
                    continue
                
                j = json.loads(f.read())
                #results is a dictionary
                results=j['results']
                if len(results)==0:
                    curSkipLst.append(i)
                    continue
                c2=0
                #parse JSON result object
                for result in results:
                    c1=c1+1
                    c2=c2+1
                    print c2,'/',c1
                    #check the integrity of the geo part of results
                    result=process(result)
                    sql="INSERT INTO "+key+" ( \
                                        id,\
                                        created_at,\
                                        from_user,\
                                        from_user_id,\
                                        to_user_id, \
                                        iso_language_code,\
                                        result_type,\
                                        profile_image_url,\
                                        source, \
                                        text, \
                                        geo_coordinates, \
                                        geo_type, \
                                        place_full_name, \
                                        place_id, \
                                        place_type, \
                                        location, \
                                        gid) \
                    VALUES (%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)"
                    
                    data=(result['id_str'], 
                    result['created_at'], 
                    result['from_user'], 
                    result['from_user_id_str'], 
                    result['to_user_id_str'], 
                    result['iso_language_code'], 
                    result['metadata']['result_type'],
                    result['profile_image_url'], 
                    result['source'], 
                    result['text'],
                    result['geo']['coordinates'],
                    result['geo']['type'],
                    result['place']['full_name'],
                    result['place']['id'],
                    result['place']['type'],
                    result['location'],
                    coord[0])
                    try:
                        cur.execute(sql, data) # Notice: no % operator
                    except:
                        continue
                conn.commit()
                #eclipse=10*random.uniform(0.2, 1.0)
                #print "sleep "+str(eclipse)+" seconds\n",
                #time.sleep(eclipse)
    
if __name__=='__main__':
    """consumer_key='TYSizU68Ks2GtCvgoRddg'
    consumer_secret='VQl8TC5zTQ0VG4JyjDOOzPGd5yIAuQTSP72gaVsWY'
    auth = tweepy.OAuthHandler(consumer_key,consumer_secret)
    token_key='75434377-vjrtPIJrkqpZdsW1gs8lKUoVoxK6LscrZ2ZepRRwN'
    token_secret='gfgGNMkcFzxiB07MMGpCtUWBl0FBfjZCZNqIg3Shg4'
    auth.set_access_token(token_key,token_secret)
    api = tweepy.API(auth)"""
    download_twitts()
    